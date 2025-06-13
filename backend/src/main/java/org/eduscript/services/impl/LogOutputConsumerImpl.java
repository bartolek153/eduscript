package org.eduscript.services.impl;

import java.util.UUID;

import org.eduscript.model.JobSession;
import org.eduscript.model.LogEntry;
import org.eduscript.repositories.JobSessionRepository;
import org.eduscript.services.LogOutputConsumer;
import org.eduscript.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class LogOutputConsumerImpl implements LogOutputConsumer {

    private final Logger logger = LoggerFactory.getLogger(LogOutputConsumerImpl.class);

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final JobSessionRepository jobSessionRepository;

    public LogOutputConsumerImpl(SimpMessagingTemplate simpMessagingTemplate,
            JobSessionRepository jobSessionRepository) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.jobSessionRepository = jobSessionRepository;
    }

    @Override
    @KafkaListener(topics = "${app.kafka.topics.logs}")
    public void consume(LogEntry log) {
        UUID jobId = Utils.uuidToStr(log.getJobId());
        jobSessionRepository.findById(jobId).ifPresentOrElse((JobSession js) -> {
            simpMessagingTemplate.convertAndSendToUser(js.getUserId().toString(), "/topic/logs", log);
        }, () -> {
            logger.error(
                    "Received log with inexistent job id {}. Could not proceed to get corresponding client",
                    jobId);
        });
    }
}
