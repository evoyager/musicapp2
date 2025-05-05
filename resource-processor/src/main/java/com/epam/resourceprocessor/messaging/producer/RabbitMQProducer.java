package com.epam.resourceprocessor.messaging.producer;

import com.epam.resourceprocessor.dto.ResourceDto;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessagePostProcessor;
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
    private final Tracer tracer;

    public void indicateResourceHasBeenProcessed(ResourceDto resource) {
        String traceId = tracer.currentSpan().context().traceId();
        String spanId = tracer.currentSpan().context().spanId();

        log.info("Trace ID in RabbitMQProducer: [{}], Span ID: [{}]", traceId, spanId);

        log.info("Indicating via RabbitMQ that resource has been processed: [{}]", resource);

        MessagePostProcessor messagePostProcessor = message -> {
            message.getMessageProperties().setHeader("x-b3-traceid", traceId);
            message.getMessageProperties().setHeader("x-b3-spanid", spanId);
            return message;
        };

        rabbitTemplate.convertAndSend(exchange, routingKey, resource, messagePostProcessor);
    }
}
