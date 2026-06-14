package com.saori.npo.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import io.jsonwebtoken.security.Keys;

@Configuration
public class JwtDecoderConfig {

    private static final String SECRET = "npo-ai-manager-phase9-jwt-secret-key-minimum-32-bytes";

    @Bean
    JwtDecoder jwtDecoder() {

        SecretKey key = Keys.hmacShaKeyFor(
                SECRET.getBytes(StandardCharsets.UTF_8));

        return NimbusJwtDecoder
                .withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS384)
                .build();
    }

}