package org.kt.hw.controller;


import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kt.hw.entity.Order;
import org.kt.hw.model.Good;
import org.kt.hw.model.OrderRequest;
import org.kt.hw.model.OrderResponse;
import org.kt.hw.repository.OrderRepository;
import org.kt.hw.saga.Coordinator;
import org.kt.hw.saga.Saga;
import org.kt.hw.service.inventory.CancelGoodsReservation;
import org.kt.hw.service.inventory.ReserveGoods;
import org.kt.hw.service.payments.CancelPayment;
import org.kt.hw.service.payments.MakePayment;
import org.kt.hw.service.shipment.CancelCourierReservation;
import org.kt.hw.service.shipment.ReserveCourier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/order")
//@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderRepository repository;
    private final Tracer tracer;

    OrderController(OrderRepository repository, OpenTelemetry openTelemetry) {
        this.tracer = openTelemetry.getTracer(getClass().getName());
        this.repository = repository;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        Span span = tracer.spanBuilder("POST /order").startSpan();
        try {
            List<Integer> goodIds = new ArrayList<>();
            int amount = 0;
            for (Good good : request.getGoods()) {
                amount += good.getPrice();
                goodIds.add(good.getId());
            }

            //span.addEvent("create order");
            span.addEvent("создание заказа");
            Order order = OrderRepository.createOrder();
            repository.save(order);

            //span.addEvent("order created");
            span.addEvent("заказ создан");
            log.debug("order created!");

            Saga saga = new Saga();
            saga.setName("order creation");
            int finalAmount = amount;
            saga.addStep(new Saga.Step(
                    "make payment",
                    () -> {
                        Span paymentSpan = tracer.spanBuilder("POST /makePayment").startSpan();
                        try {
                            log.debug("payments: start payment");
                            paymentSpan.addEvent("payments: начало оплаты");
                            MakePayment.makePayment(order.getId(), finalAmount);
                            paymentSpan.addEvent("payments: завершение оплаты");
                            log.debug("payments: end payment");
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
                            log.debug("payments: cancel payment");
                            paymentSpan.addEvent("payments: отмена оплаты");
                            CancelPayment.cancelPayment(order.getId());
                            paymentSpan.addEvent("payments: отменен");
                            log.debug("payments: canceled");
                        } catch (Exception e) {
                            paymentSpan.recordException(e);
                            throw e;
                        } finally {
                            paymentSpan.end();
                        }
                    }
            ));
            saga.addStep(new Saga.Step(
                    "reserve goods",
                    () -> {
                        log.debug("inventory: start goods reservation");
                        ReserveGoods.reserveGoods(order.getId(), goodIds);
                        log.debug("inventory: end goods reservation.");
                    },
                    () -> {
                        log.debug("inventory: cancel goods reservation");
                        CancelGoodsReservation.cancelGoodsReservation(order.getId());
                    }
            ));
            saga.addStep(new Saga.Step(
                    "reserve courier",
                    () -> {
                        log.debug("shipment: start courier reservation");
                        ReserveCourier.reserveCourier(order.getId());
                        log.debug("shipment: end courier reservation.");
                    },
                    () -> {
                        log.debug("shipment: cancel courier reservation");
                        CancelCourierReservation.cancelCourierReservation(order.getId());
                    }
            ));

            Coordinator coordinator = new Coordinator(saga);
            coordinator.commit();
            log.debug("commit success");
            return ResponseEntity.ok(new OrderResponse(order.getId()));
        } catch (Exception e) {
            log.error("commit error");
            span.addEvent("создание заказа не удалось");
            span.recordException(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } finally {
            span.end();
        }
    }


}

