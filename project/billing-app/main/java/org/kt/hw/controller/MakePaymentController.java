package org.kt.hw.controller;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.kt.hw.entity.Account;
import org.kt.hw.entity.Payment;
import org.kt.hw.model.PaymentRequest;
import org.kt.hw.repository.AccountRepository;
import org.kt.hw.repository.PaymentRepository;
import org.kt.hw.services.AccountService;
import org.kt.hw.services.exceptions.NotEnoughMoneyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/makePayment")
@Slf4j
public class MakePaymentController {


    private final AccountService accountService;
    private final PaymentRepository repository;
    private final Tracer tracer;

    MakePaymentController(AccountService accountService, PaymentRepository repository, OpenTelemetry openTelemetry) {
        this.tracer = openTelemetry.getTracer(getClass().getName());
        this.repository = repository;
        this.accountService = accountService;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> makePayment(@RequestBody PaymentRequest paymentRequest, HttpServletRequest request) {
        Span span = tracer.spanBuilder("POST /makePayment").startSpan();
        try {
            Account account = accountService.getAccount(request);
            try {
                accountService.withdraw(account, paymentRequest.getAmount());
            } catch(NotEnoughMoneyException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "not enough money");
            }

            try {
                Payment payment = PaymentRepository.createPayment(paymentRequest.getOrderId(), paymentRequest.getAmount());
                repository.save(payment);
                log.debug("payment was successful");
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } finally {
            span.end();
        }
    }
}

