package org.kt.hw.controller;


import com.google.gson.JsonObject;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.kt.hw.auth.Auth;
import org.kt.hw.entity.Order;
import org.kt.hw.model.OrderRequest;
import org.kt.hw.model.OrderResponse;
import org.kt.hw.model.User;
import org.kt.hw.repository.OrderRepository;
import org.kt.hw.saga.Coordinator;
import org.kt.hw.saga.Saga;
import org.kt.hw.service.billing.CancelPayment;
import org.kt.hw.service.billing.MakePayment;
import org.kt.hw.service.excursions.CancelExcursionReservation;
import org.kt.hw.service.excursions.GetExcursionPrice;
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

@RestController
@RequestMapping("/orders")
//@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderRepository repository;
    private final Tracer tracer;
    private final KafkaTemplate<String, String> kafkaTemplate;

    OrderController(OrderRepository repository, OpenTelemetry openTelemetry, KafkaTemplate<String, String> kafkaTemplate) {
        this.tracer = openTelemetry.getTracer(getClass().getName());
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest data, HttpServletRequest request) {
        String idempotencyKey = request.getHeader("IdempotencyKey");

/*        if (idempotencyKey == null || idempotencyKey.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "idempotency key is empty");
        }

        Order order = repository.findOrderByIdempotencyKeyEquals(idempotencyKey);
        if (order != null ) {
            // Если заказ уже был сделан, то возвращает его идентификатор
            return ResponseEntity.ok(new OrderResponse(order.getId()));
        }
*/
        User user = Auth.preHandle(request);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        String token = request.getHeader("Authorization");

        if (data == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order is empty");
        }

        // Запускаем выполнение Саги
        Span span = tracer.spanBuilder("POST /orders").startSpan();
        try {
            int price_ticket = GetExcursionPrice.getExcursionPrice(data.getLocationId(),data.getExcursionId(), data.getStart());
            int amount = price_ticket * data.getCount();

            span.addEvent("создание заказа");
            Order order = OrderRepository.createOrder(user.getId(), idempotencyKey);
            repository.save(order);

            //span.addEvent("заказ создан");
            //log.debug("order created!");

            Saga saga = new Saga();
            saga.setName("order creation");
            int orderId = order.getId();
            saga.addStep(new Saga.Step(
                    "make payment",
                    () -> {
                        Span paymentSpan = tracer.spanBuilder("POST /makePayment").startSpan();
                        try {
                            log.debug("billing: start payment");
                            paymentSpan.addEvent("billing: начало оплаты");
                            MakePayment.makePayment(orderId, amount, token);
                            paymentSpan.addEvent("billing: завершение оплаты");
                            log.debug("billing: end payment");
                        } catch (Exception e) {
                            paymentSpan.recordException(e);
                            throw e;
                        } finally {
                            paymentSpan.end();
                        }
                    },
                    () -> {
                        Span paymentSpan = tracer.spanBuilder("POST /cancelPayment").startSpan();
                        try {
                            log.debug("billing: cancel payment");
                            paymentSpan.addEvent("billing: отмена оплаты");
                            CancelPayment.cancelPayment(orderId, token);
                            paymentSpan.addEvent("billing: отменен");
                            log.debug("billing: canceled");
                        } catch (Exception e) {
                            paymentSpan.recordException(e);
                            throw e;
                        } finally {
                            paymentSpan.end();
                        }
                    }
            ));
            saga.addStep(new Saga.Step(
                    "reserve excursion",
                    () -> {
                        log.debug("excursions: start excursion reservation");
                        ReserveExcursion.reserveExcursions(orderId, data.getLocationId(), data.getExcursionId(), data.getStart(), data.getCount(), token);
                        log.debug("inventory: end goods reservation.");
                    },
                    () -> {
                        log.debug("excursions: cancel excursion reservation");
                        CancelExcursionReservation.cancelExcursionReservation(orderId, token);
                    }
            ));

            Coordinator coordinator = new Coordinator(saga);
            coordinator.commit();

            // Отправляем нотификация
            log.debug("notifications: send notification");
            JsonObject message = new JsonObject();
            message.addProperty("text", "order was created");
            message.addProperty("userId", user.getId());
            message.addProperty("locationId", data.getLocationId());
            message.addProperty("excursionId", data.getExcursionId());
            message.addProperty("start", data.getStart().toString());
            message.addProperty("count", data.getCount());
            message.addProperty("amount", amount);
            kafkaTemplate.send("notifications", message.toString());

            log.debug("commit success");
            return ResponseEntity.status(HttpStatus.CREATED).body(new OrderResponse(order.getId()));
        } catch (Exception e) {
            // Отправляем нотификация
            log.debug("notifications: send error notification");
            JsonObject message = new JsonObject();
            message.addProperty("text", "order was not created");
            message.addProperty("userId", user.getId());
            kafkaTemplate.send("notifications", message.toString());

            log.error("commit error");
            span.addEvent("создание заказа не удалось");
            span.recordException(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } finally {
            span.end();
        }
    }
}

