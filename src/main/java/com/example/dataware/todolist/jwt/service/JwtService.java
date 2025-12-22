package com.example.dataware.todolist.jwt.service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
// import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.dataware.todolist.entity.User;
import com.example.dataware.todolist.jwt.enums.TokenType;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Service
public class JwtService {

    @Value("${security.jwt.access-secret}")
    private String ACCESS_SECRET;
    @Value("${security.jwt.access-expiration}")
    private Duration ACCESS_EXP;

    @Value("${security.jwt.refresh-secret}")
    private String REFRESH_SECRET;
    @Value("${security.jwt.refresh-expiration}")
    private Duration REFRESH_EXP;

    private Key accessKey;
    private Key refreshKey;

    @PostConstruct
    private void onInit() {
        this.accessKey = Keys.hmacShaKeyFor(ACCESS_SECRET.getBytes(StandardCharsets.UTF_8));
        this.refreshKey = Keys.hmacShaKeyFor(REFRESH_SECRET.getBytes(StandardCharsets.UTF_8));
    }

    // --- Generazione token ---

    public String generateToken(User user, Key key, Duration exp) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + exp.toMillis()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateAccessToken(User user) {
        return generateToken(user, accessKey, ACCESS_EXP);
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, refreshKey, REFRESH_EXP);
    }

    // --- Estrazione claim ---

    private Claims extractAllClaims(String token, TokenType tokenType) {
        Key key = tokenType == TokenType.REFRESH ? refreshKey : accessKey;
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractEmail(String token, TokenType tokenType) {
        return extractAllClaims(token, tokenType).getSubject();
    }

    public Long extractUserId(String token, TokenType tokenType) {
        return extractAllClaims(token, tokenType)
                .get("userId", Long.class);
    }

    /**
     * Questi due metodi fanno la stessa cosa (meno pulito)
     */
    // public String extractEmail(String token) {
    // return Jwts.parserBuilder()
    // .setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8))
    // .build()
    // .parseClaimsJws(token)
    // .getBody()
    // .getSubject();
    // }

    // public Long extractUserId(String token) {
    // return Jwts.parserBuilder()
    // .setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8))
    // .build()
    // .parseClaimsJws(token)
    // .getBody()
    // .get("id", Long.class);
    // }

    /**
     * Questi due metodi fanno la stessa cosa ma usano i Generics (PROFESSIONALE)
     */
    // public <T> T extractClaim(String token, Function<Claims, T> resolver) {
    // Claims claims = Jwts.parserBuilder()
    // .setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8))
    // .build()
    // .parseClaimsJws(token)
    // .getBody();

    // return resolver.apply(claims);
    // }

    // public String extractEmail(String token) {
    // return extractClaim(token, claims -> claims.getSubject());
    // }

    // public Long extractUserId(String token) {
    // return extractClaim(token, claims -> claims.get("id", Long.class));
    // }

}
