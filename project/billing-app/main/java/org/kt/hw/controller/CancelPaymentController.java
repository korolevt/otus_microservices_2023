package org.kt.hw.controller;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.kt.hw.entity.Account;
import org.kt.hw.entity.Payment;
import org.kt.hw.model.CancelRequest;
import org.kt.hw.model.CancelResponse;
import org.kt.hw.model.PaymentRequest;
import org.kt.hw.model.PaymentResponse;
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
@RequestMapping("/cancelPayment")
@Slf4j
public class CancelPaymentController {
    private final PaymentRepository repository;
    private final AccountService accountService;
    private final Tracer tracer;

    CancelPaymentController(AccountService accountService, PaymentRepository repository, OpenTelemetry openTelemetry) {
        this.tracer = openTelemetry.getTracer(getClass().getName());
        this.repository = repository;
        this.accountService = accountService;
    }

    @PostMapping
    @Transactional
    // Возвращает стоимость оплаты
    public ResponseEntity<CancelResponse> cancelPayment(@RequestBody CancelRequest cancelRequest, HttpServletRequest request) {
        Span span = tracer.spanBuilder("POST /cancelPayment").startSpan();
        try {
            Account account = accountService.getAccount(request);
            try {
                Payment payment = repository.findPaymentByOrderId(cancelRequest.getOrderId());
                // Возвращаем средства на счет
                accountService.deposit(account, payment.getAmount());
                // Удаляем запись об оплате
                repository.deleteAllByOrderId(cancelRequest.getOrderId());
                return ResponseEntity.ok().body(new CancelResponse(cancelRequest.getOrderId(), payment.getAmount()));
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } finally {
            span.end();
        }
    }
}
