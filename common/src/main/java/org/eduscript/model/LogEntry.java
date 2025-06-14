package org.eduscript.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogEntry {
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    private LocalDateTime timestamp = LocalDateTime.now();
    private String level;
    private String message;  // TODO: convert to final fields
    private String jobId;  // TODO: convert to UUID?

    public LogEntry() {}  // required for kafka deserialization

    public LogEntry(String level, String message, String jobId) {
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

    public static DateTimeFormatter getTimeformatter() {
        return timeFormatter;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
}
