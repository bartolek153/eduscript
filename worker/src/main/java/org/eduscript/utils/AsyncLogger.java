package org.eduscript.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eduscript.logging.LogHandler;
import org.eduscript.logging.Logger;
import org.eduscript.model.LogEntry;;

// inspired by: 
// https://github.com/loki4j/loki-logback-appender/blob/main/loki-client/src/main/java/com/github/loki4j/client/pipeline/AsyncBufferPipeline.java
// and 
// https://gist.github.com/nehaev/83a5332d429e3f390f0fba740157509a

public class AsyncLogger implements LogHandler {

    private final Queue<LogEntry> buffer = new ConcurrentLinkedQueue<>();
    private final int batchSize;
    private final long flushIntervalMs;
    private final boolean useFormatter;

    private final ExecutorService executor;
    private volatile boolean running = false;

    public AsyncLogger(int batchSize, long flushIntervalMs, boolean useFormatter) {
        this.batchSize = batchSize;
        this.flushIntervalMs = flushIntervalMs;
        this.useFormatter = useFormatter;
        this.executor = Executors.newSingleThreadExecutor();
    }

    public void start() {
        running = true;
        executor.submit(this::runLoop);
    }

    public void stop() {
        running = false;
        executor.shutdown();
    }

    @Override
    public void handle(LogEntry entry) {
        buffer.add(entry);
    }

    private void runLoop() {
        List<LogEntry> batch = new ArrayList<>(batchSize);
        while (running) {
            try {
                long startTime = System.currentTimeMillis();
                batch.clear();

                while (System.currentTimeMillis() - startTime < flushIntervalMs && batch.size() < batchSize) {
                    LogEntry msg = buffer.poll();
                    if (msg != null) {
                        batch.add(msg);
                    } else {
                        Thread.sleep(10); // lower backoff
                    }
                }

                if (System.currentTimeMillis() - startTime > flushIntervalMs) {
                    System.out.println("up");
                }

                sendBatch(batch);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("Error in async logger: " + e.getMessage());
            }
        }

        List<LogEntry> remaining = new ArrayList<>();
        LogEntry msg;
        while ((msg = buffer.poll()) != null) {
            remaining.add(msg);
        }
        sendBatch(remaining);
    }

    private void sendBatch(List<LogEntry> batch) {
        if (!batch.isEmpty()) {
            for (LogEntry entry : batch) {
                String output = useFormatter ? Logger.formatEntry(entry) : rawLog(entry);
                System.out.println("[ASYNC] " + output);
            }
        }
    }

    private String rawLog(LogEntry entry) {
        return entry.getTimestamp() + " [" + entry.getLevel() + "] " + entry.getMessage();
    }

}
