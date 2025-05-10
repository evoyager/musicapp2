package com.epam.resource.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

import static org.apache.http.util.TextUtils.isEmpty;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenForwardingInterceptor implements ClientHttpRequestInterceptor {

    private final TokenContextHolder tokenContextHolder;

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        log.info("Interceptor is executing for URI: " + request.getURI());

        Optional<String> token = extractToken();

        token.ifPresent(t -> {
            request.getHeaders().add(AUTHORIZATION, "Bearer " + t);
            log.info("Added Authorization Header: Bearer " + t);
        });

        return execution.execute(request, body);
    }

    private Optional<String> extractToken() {
        var contextHolderToken = tokenContextHolder.getToken();
        log.info("Token in contextHolder: {}", contextHolderToken);
        if (isEmpty(contextHolderToken)) {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication instanceof JwtAuthenticationToken jwtToken) {
                var token = jwtToken.getToken().getTokenValue();
                tokenContextHolder.setToken(token);
                return Optional.of(token);
            }
        } else {
            return Optional.of(tokenContextHolder.getToken());
        }

        return Optional.empty();
    }
}
