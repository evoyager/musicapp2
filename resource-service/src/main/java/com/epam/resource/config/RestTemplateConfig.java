package com.epam.resource.config;

import com.epam.resource.auth.TokenForwardingInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Configuration
public class RestTemplateConfig {

    @Bean
    RestTemplate restTemplate(RestTemplateBuilder builder, TokenForwardingInterceptor tokenForwardingInterceptor) {
        RestTemplate restTemplate = builder
                .additionalInterceptors(tokenForwardingInterceptor)
                .build();
        log.info("Registered RestTemplate interceptors:");
        restTemplate.getInterceptors().forEach(interceptor -> log.info(interceptor.getClass().getName()));

        return restTemplate;
    }

}
