package org.eduscript.services.impl;

import org.eduscript.model.JobMessage;
import org.eduscript.services.SandboxExecutor;
import org.eduscript.services.JobRequestConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class JobRequestConsumerImpl implements JobRequestConsumer {

    private final Logger logger = LoggerFactory.getLogger(JobRequestConsumerImpl.class);

    private final SandboxExecutor sandboxExecutor;

    public JobRequestConsumerImpl(SandboxExecutor sandboxExecutor) {
        this.sandboxExecutor = sandboxExecutor;
    }

    @Override
    @KafkaListener(topics = "${app.kafka.topics.jobs}")
    public void consume(JobMessage job) {
        logger.debug("Compiling source: {} | id: {}",
                job.getId().toString());

        sandboxExecutor.execute(job);
    }
}
