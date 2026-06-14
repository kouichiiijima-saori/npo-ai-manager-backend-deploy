package com.saori.npo.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.saori.npo.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private static final String SECRET = "npo-ai-manager-phase9-jwt-secret-key-minimum-32-bytes";

    private static final long EXPIRATION_MILLIS = 1000L * 60L * 60L * 24L;

    public String generateToken(User user) {

        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_MILLIS);

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("role", user.getRole())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSecretKey())
                .compact();
    }

    public String extractUsername(String token) {

        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {

        return extractAllClaims(token).get("role", String.class);
    }

    public boolean isTokenValid(String token) {

        Date expiration = extractAllClaims(token).getExpiration();

        return expiration.after(new Date());
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSecretKey() {

        return Keys.hmacShaKeyFor(
                SECRET.getBytes(StandardCharsets.UTF_8));
    }

}