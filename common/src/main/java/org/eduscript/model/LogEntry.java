package org.eduscript.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogEntry {
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final LocalDateTime timestamp;
    private final String level;
    private final String message;
    private final String jobId;

    public LogEntry(String level, String message, String jobId) {
        this.timestamp = LocalDateTime.now();
        this.level = level;
        this.message = message;
        this.jobId = jobId;
    }

    public String getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getJobId() {
        return jobId;
    }

    public String timeKey() {
        return timestamp.format(timeFormatter);
    }
}
