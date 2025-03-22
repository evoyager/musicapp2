package com.epam.resourceprocessor.messaging.consumer;

import com.epam.resourceprocessor.dto.ResourceDto;
import com.epam.resourceprocessor.service.ResourceProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQConsumer {

    private final ResourceProcessorService resourceProcessorService;

    @RabbitListener(queues = "resourceQueue")
    public void listen(ResourceDto resource) {
        log.info("Resource read from resourceQueue : {}", resource);
        resourceProcessorService.getResource(resource);
    }
}
