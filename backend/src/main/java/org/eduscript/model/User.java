package org.eduscript.model;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("user")
public class User {
    @Id
    private UUID userId;
    private UUID instanceId;
    private UUID sessionId;
    private UUID runningJobId;

    public User() {
    }

    public User(
            UUID instanceId, 
            UUID sessionId,
            UUID runningJobId) {
        this.instanceId = instanceId;
        this.sessionId = sessionId;
        this.runningJobId = runningJobId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(UUID instanceId) {
        this.instanceId = instanceId;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public UUID getRunningJobId() {
        return runningJobId;
    }

    public void setRunningJobId(UUID runningJobId) {
        this.runningJobId = runningJobId;
    }
}
