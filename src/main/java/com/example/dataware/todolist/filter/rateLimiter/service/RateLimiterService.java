package com.example.dataware.todolist.filter.rateLimiter.service;

import java.time.Duration;

import java.util.function.Supplier;

import org.springframework.stereotype.Service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;

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

    public boolean isAllowed(String key, int maxRequests, long windowSeconds) {
        Bucket bucket = getBucket(key, maxRequests, windowSeconds);
        boolean allowed = bucket.tryConsume(1);
        return allowed;
    }

    public long getRemainingRequests(String key, int maxRequests, long windowSeconds) {
        Bucket bucket = getBucket(key, maxRequests, windowSeconds);
        return bucket.getAvailableTokens();
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
