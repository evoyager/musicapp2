package com.epam.resourceprocessor.messaging.producer;

import com.epam.resourceprocessor.dto.ResourceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMQProducer {

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing.key.from.resource.processor.to.service}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;

    public void indicateResourceHasBeenProcessed(ResourceDto resource) {
        log.info("Indicating via RabbitMQ that resource has been processed: [{}]", resource);
        rabbitTemplate.convertAndSend(exchange, routingKey, resource);
    }
}
