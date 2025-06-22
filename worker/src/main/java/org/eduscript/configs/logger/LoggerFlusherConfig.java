package org.eduscript.configs.logger;

import org.eduscript.services.LogOutputProducer;
import org.eduscript.utils.AsyncLoggerFlusher;
import org.eduscript.utils.LoggerFlusherProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggerFlusherConfig {

    @Bean
    AsyncLoggerFlusher asyncLoggerFlusher(
            LoggerFlusherProperties props,
            LogOutputProducer logOutputProducer) {
        
                AsyncLoggerFlusher flusher = new AsyncLoggerFlusher(
                props.getBatchSize(),
                props.getFlushIntervalMs(),
                props.isUseFormatter(),
                logOutputProducer);

        flusher.start();
        return flusher;
    }
}
