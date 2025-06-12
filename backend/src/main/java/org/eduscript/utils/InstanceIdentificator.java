package org.eduscript.utils;

import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class InstanceIdentificator {

    private final RedisTemplate<String, Object> redisTemplate;
    private static UUID id;

    private static final String INSTANCES = "instances";
    
    public InstanceIdentificator(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void registerInstance() throws Exception {
        if (id != null) { 

        }

        id = UUID.randomUUID();
        redisTemplate.opsForList().leftPush(INSTANCES, id);
    }

    public static UUID getId() {
        return id;
    }
}
