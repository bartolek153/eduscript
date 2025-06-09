package org.eduscript.services.impl;

import org.eduscript.model.JobMessage;
import org.eduscript.services.JobRequestConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class JobRequestConsumerImpl implements JobRequestConsumer {

    private final Logger logger = LoggerFactory.getLogger(JobRequestConsumerImpl.class);

    @Override
    @KafkaListener(topics = "${app.kafka.topics.jobs}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(JobMessage job) {
        logger.warn("source: {} | id: {}",
                job.getId().toString(),
                job.getSourceCode());
    }
}
