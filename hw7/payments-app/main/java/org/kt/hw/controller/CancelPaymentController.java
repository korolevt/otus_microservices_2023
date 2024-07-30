package org.kt.hw.controller;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.kt.hw.entity.Payment;
import org.kt.hw.model.CancelRequest;
import org.kt.hw.model.PaymentRequest;
import org.kt.hw.model.PaymentResponse;
import org.kt.hw.repository.PaymentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/cancelPayment")
@Slf4j
public class CancelPaymentController {
    private final PaymentRepository repository;
    private final Tracer tracer;

    CancelPaymentController(PaymentRepository repository, OpenTelemetry openTelemetry) {
        this.tracer = openTelemetry.getTracer(getClass().getName());
        this.repository = repository;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> cancelPayment(@RequestBody CancelRequest cancelRequest) {
        //Span span = tracer.spanBuilder("POST /cancelPayment").startSpan();
        //try {
            try {
                repository.deleteAllByOrderId(cancelRequest.getOrderId());
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        /*} finally {
            span.end();
        }*/
    }
}
