package com.epam.resource.auth;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Data
@Component
public class TokenContextHolder {
//    private static final ThreadLocal<String> tokenHolder = new ThreadLocal<>();

    private String token;

    public TokenContextHolder() {
        log.info("TokenContextHolder initialized");
    }

//    public static void setToken(String token) {
//        tokenHolder.set(token);
//    }
//
//    public static String getToken() {
//        return tokenHolder.get();
//    }
//
//    public static void clear() {
//        tokenHolder.remove();
//    }
}
