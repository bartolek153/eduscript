package org.eduscript.model;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "job_sessions", timeToLive = 3600)
public class JobSession {
    @Id
    private UUID jobId;
    private UUID userId;

    public JobSession() {
    }

    public JobSession(UUID jobId, UUID userId) {
        this.jobId = jobId;
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
