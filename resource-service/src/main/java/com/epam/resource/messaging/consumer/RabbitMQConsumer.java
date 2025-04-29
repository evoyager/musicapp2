package com.epam.resource.messaging.consumer;

import com.epam.resource.dto.ResourceDto;
import com.epam.resource.service.ResourceService;
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

    private final ResourceService resourceService;
    private final RetryTemplate retryTemplate;

    @RabbitListener(queues = "queue.resource.processor.to.service")
    public void listen(ResourceDto resource) {
        try {
            retryTemplate.execute((RetryCallback<Void, Exception>) context -> {
                log.info("Resource read from resourceQueue : {}", resource);
                resourceService.indicateResourceHasBeenProcessed(resource);
                return null;
            });
        } catch (Exception e) {
            log.error("All retries exhausted, sending to DLQ");
        }
    }
}
