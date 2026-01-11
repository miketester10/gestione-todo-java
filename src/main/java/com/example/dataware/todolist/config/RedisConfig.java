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
 * Configurazione per Redis con Bucket4j.
 * Configura Lettuce e ProxyManager per il rate limiting distribuito.
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

    @Bean(destroyMethod = "close")
    // Spring chiama redisConnection.close() allo stop dell'app.
    // Serve a chiudere correttamente la connessione TCP verso Redis.
    public StatefulRedisConnection<String, byte[]> redisConnection(RedisClient redisClient) {
        return redisClient.connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));
    }

    @Bean
    public ProxyManager<String> proxyManager(StatefulRedisConnection<String, byte[]> redisConnection) {
        return Bucket4jLettuce
                .casBasedBuilder(redisConnection)
                .build();
    }
}
