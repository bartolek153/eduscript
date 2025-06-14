package org.eduscript.utils;

import java.net.InetAddress;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class InstanceRegistration {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${server.port}")
    private int port;

    private static UUID id;
    private static final Logger logger = LoggerFactory.getLogger(InstanceRegistration.class);
    private static final String INSTANCE_KEY = "instance";

    public InstanceRegistration(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void registerSelf() {
        if (id != null) {
            logger.warn("Instance id already assigned before: {}", id);
            return;
        }

        id = UUID.randomUUID();
        String addr = getLocalIp() + ":" + port;

        logger.info("Assigning new instance: [id={} with address={}]", id, addr);

        redisTemplate.opsForList().leftPush(INSTANCE_KEY, id);
        redisTemplate.opsForValue().set(getInstIdKey(), addr);
    }

    public static UUID getId() {
        return id;
    }

    private String getInstIdKey() {
        return String.format("%s:%s", INSTANCE_KEY, id);
    }

    private String getLocalIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }
}
