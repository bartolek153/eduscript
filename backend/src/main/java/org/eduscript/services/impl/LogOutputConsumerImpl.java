package org.eduscript.services.impl;

import org.eduscript.model.LogEntry;
import org.eduscript.services.LogOutputConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class LogOutputConsumerImpl implements LogOutputConsumer {

    private final Logger logger = LoggerFactory.getLogger(LogOutputConsumerImpl.class);

    private final SimpMessagingTemplate simpMessagingTemplate;

    public LogOutputConsumerImpl(
            SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    @KafkaListener(topics = "${app.kafka.topics.logs}")
    public void consume(LogEntry log) {
        logger.warn("Receiving job {} log: {}", log.getJobId(), log.getMessage());
        simpMessagingTemplate.convertAndSend("/topic/logs/" + log.getJobId(), log);
    }
}
