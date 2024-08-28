package org.kt.hw.controller;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.kt.hw.repository.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/order")
//@RequiredArgsConstructor
@Slf4j
public class TestController {
    private final Tracer tracer;

    TestController(OpenTelemetry openTelemetry) {
        this.tracer = openTelemetry.getTracer(getClass().getName());

    }

    @GetMapping("/")
    public ResponseEntity<String> rootHandler() {
        log.debug("GET /");
        Span span = tracer.spanBuilder("GET /").startSpan();
        doWork();
        doWork3();
        span.end();
        log.debug("end");
        return ResponseEntity.status(HttpStatus.OK).body("Hello to order service!");
    }

    private void doWork() {
        log.debug("doWork");
        Span span = tracer.spanBuilder("doWork").startSpan();
        try {
            Thread.sleep(Duration.ofSeconds(2).toMillis());
            doWork2();
        } catch (InterruptedException e) {
            span.recordException(e);
            Thread.currentThread().interrupt();
        } finally {
            span.end();
        }
    }

    private void doWork2() {
        log.debug("doWork2");
        Span span = tracer.spanBuilder("doWork2").startSpan();
        try {
            Thread.sleep(Duration.ofSeconds(1).toMillis());
        } catch (InterruptedException e) {
            span.recordException(e);
            Thread.currentThread().interrupt();
        } finally {
           span.end();
        }
    }

    private void doWork3() {
        log.debug("doWork3");
        Span span = tracer.spanBuilder("doWork3").startSpan();
        try {
            Thread.sleep(Duration.ofSeconds(1).toMillis());
        } catch (InterruptedException e) {
            span.recordException(e);
            Thread.currentThread().interrupt();
        } finally {
            span.end();
        }
    }

}
