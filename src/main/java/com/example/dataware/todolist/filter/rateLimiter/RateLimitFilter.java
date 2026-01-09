package com.example.dataware.todolist.filter.rateLimiter;

import java.io.IOException;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.dataware.todolist.exception.ErrorResponse;
import com.example.dataware.todolist.filter.rateLimiter.enums.RateLimitEndpoint;
import com.example.dataware.todolist.filter.rateLimiter.service.RateLimiteService;
import com.example.dataware.todolist.filter.rateLimiter.service.RateLimiteService.RateLimitResult;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

/**
 * Filtro per applicare il rate limiting agli endpoint configurati.
 * Supporta rate limiting distribuito tramite Redis e Bucket4j.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimiteService rateLimiteService;
    private final ObjectMapper objectMapper;

    /**
     * Applica il filtro solo agli endpoint presenti nell'enum RateLimitEndpoint.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return RateLimitEndpoint.from(request).isEmpty();
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        Optional<RateLimitEndpoint> endpointOptional = RateLimitEndpoint.from(request);

        if (endpointOptional.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        RateLimitEndpoint endpoint = endpointOptional.get();
        String method = endpoint.getMethod().name();
        String clientIp = getClientIpAddress(request);

        String key = endpoint.name() + ":" + method + ":" + clientIp;

        final int MAX_REQUESTS = endpoint.getMaxRequests();
        final long WINDOW_SECONDS = endpoint.getWindowSeconds();

        // Verifica il rate limit e ottiene tutte le informazioni in una singola
        // chiamata
        RateLimitResult rateLimitResult = rateLimiteService.checkRateLimit(key, MAX_REQUESTS, WINDOW_SECONDS);

        // Imposta gli header di rate limit
        setRateLimitHeaders(response, MAX_REQUESTS, rateLimitResult);

        if (!rateLimitResult.isAllowed()) {
            log.warn("Rate limit exceeded for IP: {} on path: {}", clientIp, request.getRequestURI());
            sendError(response, "Too many requests. Maximum " + MAX_REQUESTS + " requests per " +
                    (WINDOW_SECONDS >= 60 ? (WINDOW_SECONDS / 60) + " minute(s)" : WINDOW_SECONDS + " second(s)")
                    + " allowed.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Estrae l'IP reale del client, considerando proxy e load balancer.
     * Controlla gli header X-Forwarded-For e X-Real-IP.
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // X-Forwarded-For può contenere una lista di IP separati da virgola
            // Il primo IP è quello del client originale
            String[] ips = xForwardedFor.split(",");
            String clientIp = ips[0].trim();
            if (!clientIp.isEmpty() && !"unknown".equalsIgnoreCase(clientIp)) {
                return clientIp;
            }
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }

        // Fallback all'IP remoto diretto
        return request.getRemoteAddr();
    }

    /**
     * Imposta gli header standard di rate limiting nella risposta HTTP.
     */
    private void setRateLimitHeaders(HttpServletResponse response, int maxRequests, RateLimitResult rateLimitResult) {
        response.setHeader("X-RateLimit-Limit", String.valueOf(maxRequests));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(rateLimitResult.getRemaining()));
        // Impostare l'header con le informazioni sul reset,
        // solo se l'endpoint è stato limitato.
        if (rateLimitResult.getResetTimeSeconds() != null) {
            response.setHeader("X-RateLimit-Reset", String.valueOf(rateLimitResult.getResetTimeSeconds()));
        }
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
