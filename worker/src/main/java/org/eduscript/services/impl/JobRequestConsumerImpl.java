package org.eduscript.services.impl;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;

import org.eduscript.model.JobMessage;
import org.eduscript.services.JobRequestConsumer;
import org.eduscript.services.SandboxJobExecutor;
import org.eduscript.services.impl.ConditionalThreadPoolServiceImpl.TaskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class JobRequestConsumerImpl implements JobRequestConsumer {

    private static final String CANCELLED_STATUS_VALUE = "CANCELLED";

    private static final String JOB_METADATA_REDIS_PREFIX = "job_metadata";

    private static final long POLL_INTERVAL_MS = 2000;
    private static final long TIMEOUT_MS = 1000 * 60 * 60;

    private final Logger logger = LoggerFactory.getLogger(JobRequestConsumerImpl.class);

    private final SandboxJobExecutor sandboxExecutor;
    private final StringRedisTemplate redisTemplate;
    private final ConditionalThreadPoolServiceImpl conditionalThreadPoolServiceImpl;

    public JobRequestConsumerImpl(
            SandboxJobExecutor sandboxExecutor,
            StringRedisTemplate redisTemplate,
            ConditionalThreadPoolServiceImpl conditionalThreadPoolServiceImpl) {
        this.sandboxExecutor = sandboxExecutor;
        this.redisTemplate = redisTemplate;
        this.conditionalThreadPoolServiceImpl = conditionalThreadPoolServiceImpl;
    }

    @Override
    @KafkaListener(topics = "${app.kafka.topics.jobs}")
    public void consume(JobMessage job) {
        logger.debug("Consuming a job message");

        CompletableFuture<TaskResult<Boolean>> result = conditionalThreadPoolServiceImpl.executeWithCondition(
                () -> {
                    return sandboxExecutor.execute(job);
                },
                checkForCancelledFlagCond(job.getId()),
                POLL_INTERVAL_MS,
                TIMEOUT_MS);

        try {
            if (result.get().isCancelled()) {

            }
        } catch (Exception e) {
            System.err.println("Error waiting for result: " + e.getMessage());
        }

    }

    private BooleanSupplier checkForCancelledFlagCond(UUID jobId) {
        String key = String.format("%s:%s", JOB_METADATA_REDIS_PREFIX, jobId); // TODO: remove hardcode

        BooleanSupplier customCondition = () -> {
            var ret = redisTemplate.opsForHash().get(key, "status");
            return CANCELLED_STATUS_VALUE.equals(ret) ? true : false;
        };

        return customCondition;
    }
}
