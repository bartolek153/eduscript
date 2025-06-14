package org.eduscript.services.impl;

import org.eduscript.model.JobMessage;
import org.eduscript.services.CompileService;
import org.eduscript.services.JobRequestConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class JobRequestConsumerImpl implements JobRequestConsumer {

    private final Logger logger = LoggerFactory.getLogger(JobRequestConsumerImpl.class);

    private final CompileService compileService;

    public JobRequestConsumerImpl(CompileService compileService) {
        this.compileService = compileService;
    }

    @Override
    @KafkaListener(topics = "${app.kafka.topics.jobs}")
    public void consume(JobMessage job) {
        logger.warn("Compiling source: {} | id: {}",
                job.getId().toString(),
                job.getSourceCode());

        compileService.compile(job);
    }
}
