package com.example.dataware.todolist.config;

import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.Bucket4jLettuce;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI.Builder;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configurazione di Redis per l'applicazione.
 * 
 * Fornisce:
 * 1. RedisClient generico per la connessione al server Redis.
 * 2. Connessione per Bucket4j (String -> byte[]) per il rate limiting
 * distribuito.
 * 3. Connessione per dati applicativi leggibili (String -> String) per token,
 * JSON e oggetti.
 * 
 * I bean creati gestiscono automaticamente la chiusura delle connessioni e
 * delle risorse
 * all'arresto dell'applicazione.
 */
@Slf4j
@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.password}")
    private String redisPassword;

    // --------------------------
    // Connessione Lettuce generica
    // --------------------------
    @Bean(destroyMethod = "shutdown")
    // Spring chiama redisClient.shutdown() allo stop dell'app.
    // Serve a chiudere correttamente thread ed event-loop interni di Lettuce
    // ed evitare memory leak.
    public RedisClient redisClient() {
        Builder uriBuilder = Builder
                .redis(redisHost)
                .withPort(redisPort);

        if (redisPassword != null && !redisPassword.isEmpty()) {
            uriBuilder.withPassword(redisPassword.toCharArray());
        }

        return RedisClient.create(uriBuilder.build());
    }

    // --------------------------
    // Connessione per Bucket4j
    // Key -> String
    // Value -> byte[]
    // --------------------------
    @Bean(destroyMethod = "close")
    // Spring chiama redisConnection.close() allo stop dell'app.
    // Serve a chiudere correttamente la connessione TCP verso Redis.
    public StatefulRedisConnection<String, byte[]> redisConnectionBucket4j(RedisClient redisClient) {
        return redisClient.connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));
    }

    @Bean
    public ProxyManager<String> proxyManager(StatefulRedisConnection<String, byte[]> redisConnectionBucket4j) {
        return Bucket4jLettuce
                .casBasedBuilder(redisConnectionBucket4j)
                .build();
    }

    // --------------------------
    // Connessione per dati applicativi leggibili (token verifica email, json,
    // oggetti, ecc..)
    // Key -> String
    // Value -> String
    // --------------------------
    @Bean(destroyMethod = "close")
    public StatefulRedisConnection<String, String> redisConnectionApp(RedisClient redisClient) {
        return redisClient.connect(RedisCodec.of(StringCodec.UTF8, StringCodec.UTF8));
    }
}
