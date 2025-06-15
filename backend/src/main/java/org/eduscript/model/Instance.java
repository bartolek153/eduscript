package org.eduscript.model;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("instance")
public class Instance {
    @Id
    private UUID id;
    private String host;
    private Integer port;

    public Instance() {
    }

    public Instance(UUID id, String host, Integer port) {
        this.id = id;
        this.host = host;
        this.port = port;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
