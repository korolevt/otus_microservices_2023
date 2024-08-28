package org.kt.hw.controller;


import com.google.gson.JsonObject;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.kt.hw.auth.Session;
import org.kt.hw.entity.Order;
import org.kt.hw.model.*;
import org.kt.hw.repository.OrderRepository;
import org.kt.hw.saga.Coordinator;
import org.kt.hw.saga.Saga;
import org.kt.hw.service.billing.CancelPayment;
import org.kt.hw.service.billing.MakePayment;
import org.kt.hw.service.excursions.CancelExcursionReservation;
import org.kt.hw.service.excursions.GetReservedExcursions;
import org.kt.hw.service.excursions.ReserveExcursion;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@Slf4j
public class ExcursionCancellationController {

    private final OrderRepository repository;
    private final Tracer tracer;
    private final KafkaTemplate<String, String> kafkaTemplate;

    ExcursionCancellationController(OrderRepository repository, OpenTelemetry openTelemetry, KafkaTemplate<String, String> kafkaTemplate) {
        this.tracer = openTelemetry.getTracer(getClass().getName());
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("excursionCancellation")
    @Transactional
    public ResponseEntity<?> excursionCancellation(@RequestBody CancellationRequest data, HttpServletRequest request) {
        String idempotencyKey = request.getHeader("IdempotencyKey");

        if (data == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data is empty");
        }

        // Запускаем выполнение Саги
        Span span = tracer.spanBuilder("POST /excursionCancellation").startSpan();
        try {

            List<Integer> orderIDs = GetReservedExcursions.getReservedExcursions(data.getLocationId(),data.getExcursionId(), data.getStart());
            List<Order> orders = repository.findAllById(orderIDs);
            Map<Integer, String> tokens = new HashMap<>();
            for (Order order: orders) {
                String token = "Bearer " + Session.createAccessToken(order.getUserId());
                tokens.put(order.getId(), token);
            }

            span.addEvent("отмена экскурсии");

            Saga saga = new Saga();
            saga.setName("order creation");
            // Отмененные платежи
            Map<Integer, Payment> payments = new HashMap<>();
            Map<Integer, Excursion> excursions = new HashMap();
            saga.addStep(new Saga.Step(
                    "cancel payment",
                    () -> {
                        Span paymentSpan = tracer.spanBuilder("POST /cancelPayment").startSpan();
                        try {
                            for (Order order: orders) {
                                log.debug("billing: cancel payment");
                                paymentSpan.addEvent("billing: отмена оплаты");
                                Payment payment = CancelPayment.cancelPayment(order.getId(), tokens.get(order.getId()));
                                payments.put(order.getId(), payment);
                                paymentSpan.addEvent("billing: отменен");
                                log.debug("billing: canceled");
                            }

                        } catch (Exception e) {
                            paymentSpan.recordException(e);
                            throw e;
                        } finally {
                            paymentSpan.end();
                        }
                    },
                    () -> {
                        Span paymentSpan = tracer.spanBuilder("POST /makePayment").startSpan();
                        try {
                            for (Order order: orders) {
                                Payment payment = payments.get(order.getId());
                                if (payment != null) {
                                    log.debug("billing: start payment");
                                    paymentSpan.addEvent("billing: начало оплаты");
                                    MakePayment.makePayment(payment.getOrderId(), payment.getAmount(), tokens.get(payment.getOrderId()));
                                    paymentSpan.addEvent("billing: завершение оплаты");
                                    log.debug("billing: end payment");
                                }
                            }
                        } catch (Exception e) {
                            paymentSpan.recordException(e);
                            throw e;
                        } finally {
                            paymentSpan.end();
                        }
                    }
            ));
            saga.addStep(new Saga.Step(
                    "cancellation excursion",
                    () -> {
                        for (Order order: orders) {
                            log.debug("excursions: start cancellation excursion");
                            Excursion excursion = CancelExcursionReservation.cancelExcursionReservation(order.getId(), tokens.get(order.getId()));
                            excursions.put(order.getId(), excursion);
                            log.debug("excursions: end cancellation excursion.");
                        }
                    },
                    () -> {
                        for (Order order: orders) {
                            Excursion excursion = excursions.get(order.getId());
                            if (excursion != null) {
                                log.debug("excursions: cancel cancellation excursion");
                                ReserveExcursion.reserveExcursions(excursion.getOrderId(), data.getLocationId(),
                                        data.getExcursionId(), data.getStart(), excursion.getCount(), tokens.get(excursion.getOrderId()));
                            }
                        }

                    }
            ));

            Coordinator coordinator = new Coordinator(saga);
            coordinator.commit();

            // Удаляем заказы
            for (Order order: orders) {
                repository.deleteById(order.getId());
            }

            // Отправляем нотификации всем клиентам об отмене заказа
            log.debug("notifications: send notification");
            for (Order order: orders) {
                JsonObject message = new JsonObject();
                message.addProperty("text", "order was cancellation");
                message.addProperty("userId", order.getUserId());
                message.addProperty("locationId", data.getLocationId());
                message.addProperty("excursionId", data.getExcursionId());
                message.addProperty("start", data.getStart().toString());
                message.addProperty("count", excursions.get(order.getId()).getCount());
                message.addProperty("amount", payments.get(order.getId()).getAmount());
                kafkaTemplate.send("notifications", message.toString());
            }

            log.debug("commit success");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            span.recordException(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } finally {
            span.end();
        }
    }
}

