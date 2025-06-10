package org.eduscript.configs;

import org.eduscript.logging.Logger;
import org.eduscript.utils.AsyncLoggerFlusher;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Configuration
public class LoggerInitializer {

    private final AsyncLoggerFlusher asyncLoggerFlusher;

    public LoggerInitializer(AsyncLoggerFlusher asyncLoggerFlusher) {
        this.asyncLoggerFlusher = asyncLoggerFlusher;
    }

    @PostConstruct
    public void init() {
        Logger.addHandler(asyncLoggerFlusher);
    }

    @PreDestroy
    public void stop() {
        Logger.clearHandlers();
    }
}

