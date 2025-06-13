package org.eduscript.model;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "user_sessions", timeToLive = 3600)
public class UserSession {
    @Id
    private UUID userId;
    private UUID instanceId;
    private UUID sessionId;

    public UserSession() {
    }

    public UserSession(
            UUID userId,
            UUID instanceId, 
            UUID sessionId) {
        this.userId = userId;
        this.instanceId = instanceId;
        this.sessionId = sessionId;
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
}
