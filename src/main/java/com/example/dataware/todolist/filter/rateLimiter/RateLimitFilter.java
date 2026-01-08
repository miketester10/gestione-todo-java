package com.example.dataware.todolist.filter.rateLimiter;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.dataware.todolist.exception.ErrorResponse;
import com.example.dataware.todolist.filter.rateLimiter.service.RateLimiterService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

/**
 * Filtro per applicare il rate limiting agli endpoint di autenticazione.
 * Limita a 4 richieste per minuto per:
 * - /auth/login
 * - /auth/logout
 * - /auth/register
 * - /auth/refresh-token
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimiterService rateLimiterService;
    private final ObjectMapper objectMapper;

    private static final int MAX_REQUESTS = 4;
    private static final long WINDOW_SECONDS = 60; // 1 minuto

    /**
     * Applica il filtro solo agli endpoint di autenticazione.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.startsWith("/auth");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String clientIp = request.getRemoteAddr();
        String identifier = "auth:" + clientIp;

        if (!rateLimiterService.isAllowed(identifier, MAX_REQUESTS, WINDOW_SECONDS)) {
            log.warn("Rate limit exceeded for IP: {} on path: {}", clientIp, request.getRequestURI());

            long remaining = rateLimiterService.getRemainingRequests(identifier, MAX_REQUESTS, WINDOW_SECONDS);

            response.setHeader("X-RateLimit-Limit", String.valueOf(MAX_REQUESTS));
            response.setHeader("X-RateLimit-Remaining", String.valueOf(remaining));
            response.setHeader("X-RateLimit-Reset", String.valueOf(System.currentTimeMillis() / 1000 + WINDOW_SECONDS));

            sendError(response, "Too many requests. Maximum " + MAX_REQUESTS + " requests per minute allowed.");
            return;
        }

        // Aggiunge gli header di rate limit anche in caso di successo
        long remaining = rateLimiterService.getRemainingRequests(identifier, MAX_REQUESTS, WINDOW_SECONDS);
        response.setHeader("X-RateLimit-Limit", String.valueOf(MAX_REQUESTS));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(remaining));
        response.setHeader("X-RateLimit-Reset", String.valueOf(System.currentTimeMillis() / 1000 + WINDOW_SECONDS));

        filterChain.doFilter(request, response);
    }

    private void sendError(HttpServletResponse response, String message) throws IOException {
        HttpStatus status = HttpStatus.TOO_MANY_REQUESTS;

        ErrorResponse errorResponseObj = ErrorResponse.builder()
                .statusCode(status.value())
                .reason(status.getReasonPhrase())
                .message(message)
                .build();

        String errorResponseString = objectMapper.writeValueAsString(errorResponseObj);

        response.setContentType(MediaType.APPLICATION_JSON.toString());
        response.setStatus(status.value());
        response.getWriter().write(errorResponseString);
    }
}
