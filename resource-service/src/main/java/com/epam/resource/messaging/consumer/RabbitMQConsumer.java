package com.epam.resource.messaging.consumer;

import com.epam.resource.dto.ResourceDto;
import com.epam.resource.service.ResourceService;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQConsumer {

    private final ResourceService resourceService;
    private final RetryTemplate retryTemplate;
    private final Tracer tracer;
    private final Propagator propagator;

    @RabbitListener(queues = "queue.resource.processor.to.service")
    public void listen(ResourceDto resource, @Headers Map<String, Object> headers) {
        String traceId = (String) headers.get("x-b3-traceid");
        String spanId = (String) headers.get("x-b3-spanid");

        log.info("Trace ID in RabbitMQConsumer: [{}], Span ID: [{}]", traceId, spanId);

        Span span = propagator.extract(headers, (carrier, key) -> {
            Object value = carrier.get(key.toLowerCase());
            return value != null ? value.toString() : null; // Convert Object to String
        }).start();
        try (Tracer.SpanInScope spanInScope = tracer.withSpan(span)) {
            retryTemplate.execute((RetryCallback<Void, Exception>) context -> {
                log.info("Resource read from resourceQueue : {}", resource);
                resourceService.indicateResourceHasBeenProcessed(resource);
                return null;
            });
        } catch (Exception e) {
            log.error("All retries exhausted, sending to DLQ. Error processing RabbitMQ message: " + e.getMessage());
        }
    }
}
