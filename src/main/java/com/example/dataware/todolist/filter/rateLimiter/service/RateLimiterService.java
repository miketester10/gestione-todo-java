package com.example.dataware.todolist.filter.rateLimiter.service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.proxy.ProxyManager;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementazione del servizio di rate limiting utilizzando Bucket4j con Redis.
 * Bucket4j gestisce automaticamente il rate limiting distribuito.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final ProxyManager<String> proxyManager;

    /**
     * Risultato della verifica del rate limit.
     * Contiene tutte le informazioni necessarie per gli header di risposta.
     */
    @Getter
    @RequiredArgsConstructor
    public static class RateLimitResult {
        private final boolean allowed;
        private final long remaining;
        private final Long resetTimeSeconds;
    }

    /**
     * Verifica se la richiesta è consentita e restituisce tutte le informazioni
     * necessarie per gli header di risposta in una singola chiamata.
     */
    public RateLimitResult checkRateLimit(String key, int maxRequests, long windowSeconds) {
        Bucket bucket = getBucket(key, maxRequests, windowSeconds);

        // Usa tryConsumeAndReturnRemaining per ottenere tutte le info in una chiamata
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        boolean allowed = probe.isConsumed();
        long remaining = Math.max(probe.getRemainingTokens(), 0); // Difensivo, ci assicuriamo che il numero non sia mai negativo.

        // Calcola il reset time solo se l'endpoint è stato limitato,
        // basandosi sul prossimo refill
        Long resetTimeSeconds = null;
        if (!probe.isConsumed()) {
            resetTimeSeconds = Instant.now().getEpochSecond()
                    + TimeUnit.NANOSECONDS.toSeconds(
                            probe.getNanosToWaitForRefill());
        }

        return new RateLimitResult(allowed, remaining, resetTimeSeconds);
    }

    /**
     * Ottiene o crea un bucket per la key Redis specificata.
     * Bucket4j gestisce automaticamente la creazione e la sincronizzazione
     * distribuita.
     */
    private Bucket getBucket(String key, int maxRequests, long windowSeconds) {
        Supplier<BucketConfiguration> configSupplier = () -> {
            // Usa Bandwidth.builder per creare un bandwidth con refill continuo
            Bandwidth limit = Bandwidth.builder()
                    .capacity(maxRequests)
                    .refillIntervally(maxRequests, Duration.ofSeconds(windowSeconds))
                    .build();
            return BucketConfiguration.builder()
                    .addLimit(limit)
                    .build();
        };

        return proxyManager.builder()
                .build(key, configSupplier);
    }

}
