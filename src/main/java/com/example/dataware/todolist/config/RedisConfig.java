package com.example.dataware.todolist.config;

import java.lang.reflect.Field;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.command.CommandAsyncExecutor;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.redisson.cas.RedissonBasedProxyManager;
import lombok.extern.slf4j.Slf4j;

/**
 * Configurazione per Redis con Bucket4j.
 * Configura Redisson e ProxyManager per il rate limiting distribuito.
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
    public RedissonClient redissonClient() {
        Config config = new Config();
        SingleServerConfig singleServerConfig = config
                .useSingleServer()
                .setAddress("redis://" + redisHost + ":" + redisPort);

        if (redisPassword != null && !redisPassword.isEmpty()) {
            singleServerConfig.setPassword(redisPassword);
        }

        return Redisson.create(config);
    }

    @Bean
    public ProxyManager<String> proxyManager(RedissonClient redissonClient) throws Exception {

        // Usa reflection per ottenere il CommandAsyncExecutor interno
        // L'implementazione concreta di RedissonClient contiene un campo
        // commandExecutor
        Field executorField = redissonClient.getClass()
                .getDeclaredField("commandExecutor");
        executorField.setAccessible(true);
        CommandAsyncExecutor executor = (CommandAsyncExecutor) executorField.get(redissonClient);

        return RedissonBasedProxyManager
                .builderFor(executor)
                .build();

    }

}
