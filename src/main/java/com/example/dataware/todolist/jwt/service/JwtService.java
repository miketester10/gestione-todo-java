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

@Service
public class JwtService {
    private final Key accessKey;
    private final Duration accessExp;
    private final Key refreshKey;
    private final Duration refreshExp;

    public JwtService(
            @Value("${security.jwt.access-secret}") String ACCESS_SECRET,
            @Value("${security.jwt.access-expiration}") Duration ACCESS_EXP,
            @Value("${security.jwt.refresh-secret}") String REFRESH_SECRET,
            @Value("${security.jwt.refresh-expiration}") Duration REFRESH_EXP) {
        this.accessKey = Keys.hmacShaKeyFor(ACCESS_SECRET.getBytes(StandardCharsets.UTF_8));
        this.accessExp = ACCESS_EXP;
        this.refreshKey = Keys.hmacShaKeyFor(REFRESH_SECRET.getBytes(StandardCharsets.UTF_8));
        this.refreshExp = REFRESH_EXP;

    }

    // --- Generazione token ---

    public String generateToken(User user, Key key, Duration exp) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + exp.toMillis()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateAccessToken(User user) {
        return generateToken(user, accessKey, accessExp);
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, refreshKey, refreshExp);
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

    public String extractRole(String token, TokenType tokenType) {
        return extractAllClaims(token, tokenType)
                .get("role", String.class);
    }

    /**
     * Questo metodo utilizza i Generics e le Lambda Expressions per estrarre
     * dinamicamente un claim dal token JWT.
     *
     * Il parametro `resolver` è una lambda che prende in input un oggetto
     * `Claims` e restituisce un valore di tipo `T`.
     *
     * Esempi di resolver:
     * - claims -> claims.getSubject() // restituisce l'email
     * - claims -> claims.get("id", Long.class) // restituisce l'id utente
     *
     * Il metodo estrae i claims dal token e poi invoca la funzione passata
     * come argomento tramite `resolver.apply(claims)`.
     */

    // public <T> T extractClaim(String token, TokenType tokenType, Function<Claims,
    // T> resolver) {

    // // Estrazione di tutti i claims dal token
    // Claims claims = extractAllClaims(token, tokenType);

    // // Invocazione della funzione resolver per ottenere il claim desiderato
    // return resolver.apply(claims);
    // }

    // public String extractEmail(String token, TokenType tokenType) {
    // return extractClaim(token, tokenType, claims -> claims.getSubject());
    // }

    // public Long extractUserId(String token, TokenType tokenType) {
    // return extractClaim(token, tokenType, claims -> claims.get("id",
    // Long.class));
    // }

}
