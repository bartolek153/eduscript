package org.eduscript.utils;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class InstanceIdentificator {

    private final RedisTemplate<String, Object> redisTemplate;
    private static UUID id;
    private static final Logger logger = LoggerFactory.getLogger(InstanceIdentificator.class);

    private static final String INSTANCES_KEYS = "instances";
    
    public InstanceIdentificator(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void registerInstance() {
        if (id != null) { 
            logger.warn("Instance id already assigned before: {}", id);
            return;
        }

        id = UUID.randomUUID();
        logger.info("Assigning new instance id: {}", id);
        // redisTemplate.opsForList().leftPush(INSTANCES_KEYS, id);
    }

    public static UUID getId() {
        return id;
    }
}
