package com.example.dataware.todolist.filter.rateLimiter;

import java.io.IOException;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.dataware.todolist.exception.ErrorResponse;
import com.example.dataware.todolist.filter.rateLimiter.enums.RateLimitEndpoint;
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

    /**
     * Applica il filtro solo agli endpoint presenti nell'enum RateLimitEndpoint.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return RateLimitEndpoint.fromPath(path).isEmpty();
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        Optional<RateLimitEndpoint> endpointOptional = RateLimitEndpoint.fromPath(path);

        if (endpointOptional.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        RateLimitEndpoint endpoint = endpointOptional.get();
        String clientIp = request.getRemoteAddr();
        String key = endpoint.name() + ":" + clientIp;

        final int MAX_REQUESTS = endpoint.getMaxRequests();
        final long WINDOW_SECONDS = endpoint.getWindowSeconds();

        if (!rateLimiterService.isAllowed(key, MAX_REQUESTS, WINDOW_SECONDS)) {
            log.warn("Rate limit exceeded for IP: {} on path: {}", clientIp, request.getRequestURI());

            long remaining = rateLimiterService.getRemainingRequests(key, MAX_REQUESTS, WINDOW_SECONDS);

            response.setHeader("X-RateLimit-Limit", String.valueOf(MAX_REQUESTS));
            response.setHeader("X-RateLimit-Remaining", String.valueOf(remaining));
            response.setHeader("X-RateLimit-Reset", String.valueOf(System.currentTimeMillis() / 1000 + WINDOW_SECONDS));

            sendError(response, "Too many requests. Maximum " + MAX_REQUESTS + " requests per minute allowed.");
            return;
        }

        // Aggiunge gli header di rate limit anche in caso di successo
        long remaining = rateLimiterService.getRemainingRequests(key, MAX_REQUESTS, WINDOW_SECONDS);
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
