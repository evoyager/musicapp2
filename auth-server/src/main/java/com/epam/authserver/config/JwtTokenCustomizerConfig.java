package com.epam.authserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenClaimsContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.stream.Collectors;

@Configuration
public class JwtTokenCustomizerConfig {

    @Bean
    public OAuth2TokenCustomizer<OAuth2TokenClaimsContext> tokenCustomizer() {
        return context -> {
            Authentication principal = context.getPrincipal(); // Get the authenticated principal

            if (principal != null && principal.getAuthorities() != null) {
                // Extract roles from the principal's authorities and add to token claims
                context.getClaims().claim("roles", principal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority) // Get authorities as strings
                        .filter(role -> role.startsWith("ROLE_")) // Include only roles with prefix ROLE_
                        .collect(Collectors.toList()));
            }
        };
    }

}
