package org.kt.hw.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.exporter.jaeger.JaegerGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import io.opentelemetry.semconv.ResourceAttributes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

// https://naveen-metta.medium.com/implementing-opentelemetry-for-spring-boot-microservices-2c2d857a4e65


@Configuration
@Slf4j
public class OpenTelemetryConfig {

    private final static String serviceName = "order_service";

    @Bean
    public OpenTelemetry openTelemetry() {
        SpanExporter exporter = newExporter(System.getenv("TRACER_PROVIDER"));


        Resource serviceNameResource =
                Resource.create(Attributes.of(ResourceAttributes.SERVICE_NAME, serviceName));

        SdkTracerProvider tp = SdkTracerProvider.builder()
                .addSpanProcessor(BatchSpanProcessor.builder(exporter).build())
                .setResource(Resource.getDefault().merge(serviceNameResource))
                .build();

        OpenTelemetrySdk openTelemetry =
                OpenTelemetrySdk.builder().setTracerProvider(tp).build();

        // it's always a good idea to shut down the SDK cleanly at JVM exit.
        Runtime.getRuntime().addShutdownHook(new Thread(tp::close));

        return openTelemetry;
    }

/*    private static OtlpGrpcSpanExporter newExporter(String url) {
        return OtlpGrpcSpanExporter.builder()
                .setEndpoint(url)
                //.setTimeout(30, TimeUnit.SECONDS)
                .build();
    }*/

    private static JaegerGrpcSpanExporter newExporter(String url) {
        return JaegerGrpcSpanExporter.builder()
                .setEndpoint(url)
                .setTimeout(30, TimeUnit.SECONDS)
                .build();
    }
}

