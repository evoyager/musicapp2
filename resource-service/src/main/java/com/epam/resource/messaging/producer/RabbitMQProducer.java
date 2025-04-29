package com.epam.resource.messaging.producer;

import com.epam.resource.dto.ResourceDto;
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

    @Value("${rabbitmq.routing.key.from.resource.service.to.processor}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;

    public void sendResourceIdMessage(ResourceDto resource) {
        log.info("Sending resource to RabbitMQ: {}", resource);
        rabbitTemplate.convertAndSend(exchange, routingKey, resource);
    }
}
