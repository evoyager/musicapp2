package com.epam.resource.config;

import com.epam.resource.exceptions.listener.DefaultListenerSupport;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

// tag::encoder[]
@Configuration
@EnableRetry
public class AppConfig {

    @Bean
    RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

//    @Bean
//    BytesEncoder<MutableSpan> otlpMutableSpanBytesEncoder() {
//        return OtlpProtoV1Encoder.create();
//    }

//    @Bean
//    public Tracing createTracing() {
//        return Tracing.newBuilder()
//                .propagationFactory(B3Propagation.FACTORY)
//                .localServiceName("resource-service")
//                .build();
//    }

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(2000l);
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(2);
        retryTemplate.setRetryPolicy(retryPolicy);

        retryTemplate.registerListener(new DefaultListenerSupport() {});

        return retryTemplate;
    }
}
