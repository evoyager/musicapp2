package com.epam.resourceprocessor.messaging.consumer;

import com.epam.resourceprocessor.dto.ResourceDto;
import com.epam.resourceprocessor.service.ResourceProcessorService;
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

    private final ResourceProcessorService resourceProcessorService;
    private final RetryTemplate retryTemplate;
    private final Tracer tracer;
    private final Propagator propagator;

    @RabbitListener(queues = "queue.resource.service.to.processor")
    public void listen(ResourceDto resource, @Headers Map<String, Object> headers) {
        log.info("Processing resource: [{}]", resource);
        log.info("Raw RabbitMQ Headers: {}", headers);

        String traceId = (String) headers.get("x-b3-traceid");
        String spanId = (String) headers.get("x-b3-spanid");

        log.info("Trace ID in RabbitMQConsumer: [{}], Span ID: [{}]", traceId, spanId);

        Span span = propagator.extract(headers, (carrier, key) -> {
            log.info("Checking header key: {}", key);
            Object value = carrier.get(key.toLowerCase());
            log.info("Found value for key [{}]: {}", key, value);
            return value != null ? value.toString() : null; // Convert Object to String
        }).start();

//        Span span = propagator.extract(headers, (carrier, key) -> {
//            return headers.containsKey(key.toLowerCase()) ? (String) headers.get(key.toLowerCase()) : null; // Ensure headers are Strings
//        }).start();

        try {
            retryTemplate.execute((RetryCallback<Void, Exception>) context -> {
                // Simulate a failure in processing
                if (context.getRetryCount() < 1) { // Fail first time
                    log.warn("Simulated failure, retrying...");
                    throw new Exception("Failed processing message");
                }

                log.info("Processed successfully at retry count: " + context.getRetryCount());
                log.info("Resource read from resourceQueue : {}", resource);
                try (Tracer.SpanInScope spanInScope = tracer.withSpan(span)) {
                    log.info("Trace ID inside RabbitMQConsumer try block: [{}], Span ID: [{}]",
                            span.context().traceId(), span.context().spanId());
                    resourceProcessorService.getAudioDataExtractMetadataAndSendToSongService(resource);
                } catch (Exception e) {
                    System.err.println("Error processing RabbitMQ message: " + e.getMessage());
                }
                return null;  // Processing succeeded
            });
        } catch (Exception e) {
            log.error("All retries exhausted, sending to DLQ, error message: [{}]", e.getMessage(), e);
        }
    }
}
