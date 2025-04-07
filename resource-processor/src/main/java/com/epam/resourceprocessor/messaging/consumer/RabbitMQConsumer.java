package com.epam.resourceprocessor.messaging.consumer;

import com.epam.resourceprocessor.dto.ResourceDto;
import com.epam.resourceprocessor.service.ResourceProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQConsumer {

    private final ResourceProcessorService resourceProcessorService;
    private final RetryTemplate retryTemplate;

    @RabbitListener(queues = "resourceQueue")
    public void listen(ResourceDto resource) {
        try {
            retryTemplate.execute((RetryCallback<Void, Exception>) context -> {
                // Simulate a failure in processing
                if (context.getRetryCount() < 2) { // Fail first few times
                    log.warn("Simulated failure, retrying...");
                    throw new Exception("Failed processing message");
                }

                log.info("Processed successfully at retry count: " + context.getRetryCount());
                log.info("Resource read from resourceQueue : {}", resource);
                resourceProcessorService.getResource(resource);
                return null;  // Processing succeeded
            });
        } catch (Exception e) {
            log.error("All retries exhausted, sending to DLQ");
        }
    }
}
