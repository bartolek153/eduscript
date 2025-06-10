package org.eduscript.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.logger")
public class LoggerFlusherProperties {
    private int batchSize = 10;
    private long flushIntervalMs = 1000;
    private boolean useFormatter = true;

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public long getFlushIntervalMs() {
        return flushIntervalMs;
    }

    public void setFlushIntervalMs(long flushIntervalMs) {
        this.flushIntervalMs = flushIntervalMs;
    }

    public boolean isUseFormatter() {
        return useFormatter;
    }

    public void setUseFormatter(boolean useFormatter) {
        this.useFormatter = useFormatter;
    }
}
