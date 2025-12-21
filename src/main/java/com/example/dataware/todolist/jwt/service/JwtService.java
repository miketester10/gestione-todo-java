package com.example.dataware.todolist.jwt.service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
// import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.dataware.todolist.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String SECRET;
    @Value("${security.jwt.expiration-time}")
    private long EXP;

    private Key key;

    @PostConstruct
    private void onInit() {
        this.key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXP))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Long extractUserId(String token) {
        return extractAllClaims(token)
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
