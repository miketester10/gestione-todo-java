package com.example.dataware.todolist.service;

import org.springframework.stereotype.Service;

import io.lettuce.core.api.StatefulRedisConnection;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

/**
 * Servizio universale per operazioni su Redis.
 * 
 * Supporta:
 * - Salvataggio di valori generici (String, numeri, oggetti, JSON, liste,
 * mappe) con TTL opzionale.
 * - Lettura dei valori deserializzati nel tipo corretto.
 * - Cancellazione di chiavi.
 * 
 * Utilizza ObjectMapper per la serializzazione/deserializzazione JSON.
 */

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StatefulRedisConnection<String, String> redisConnectionApp;
    private final ObjectMapper objectMapper;

    /**
     * Salva un valore generico su Redis.
     * 
     * @param key        la chiave Redis
     * @param value      il valore da salvare (qualsiasi tipo)
     * @param ttlSeconds opzionale: se >0, il valore scade dopo ttlSeconds
     * @param <T>        tipo del valore
     */
    public <T> void set(String key, T value, long ttlSeconds) {
        try {
            String json = objectMapper.writeValueAsString(value);
            if (ttlSeconds > 0) {
                redisConnectionApp.sync().setex(key, ttlSeconds, json);
            } else {
                redisConnectionApp.sync().set(key, json);
            }
        } catch (Exception e) {
            throw new RuntimeException("Errore serializzazione Redis per key: " + key, e);
        }
    }

    /**
     * Legge un valore generico da Redis.
     * 
     * @param key         la chiave Redis
     * @param targetClass la classe del tipo da leggere
     * @param <T>         tipo del valore
     * @return il valore deserializzato o null se la chiave non esiste
     */
    public <T> T get(String key, Class<T> targetClass) {
        String json = redisConnectionApp.sync().get(key);
        if (json == null)
            return null;

        try {
            return objectMapper.readValue(json, targetClass);
        } catch (Exception e) {
            throw new RuntimeException("Errore deserializzazione Redis per key: " + key, e);
        }
    }

    /**
     * Cancella una chiave da Redis.
     * 
     * @param key la chiave da cancellare
     */
    public void delete(String key) {
        redisConnectionApp.sync().del(key);
    }

}
