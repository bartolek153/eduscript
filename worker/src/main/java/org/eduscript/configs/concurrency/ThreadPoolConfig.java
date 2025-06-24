package org.eduscript.configs.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PreDestroy;

@Configuration
public class ThreadPoolConfig {
    @Bean("cachedTPool")
    public ExecutorService defaultExecutor() {
        return Executors.newCachedThreadPool();
    }

    @PreDestroy
    public void shutdown() {
        defaultExecutor().shutdown();
    }
}
