package org.eduscript.services.impl;

import java.util.concurrent.CompletableFuture;

import org.eduscript.model.LogEntry;
import org.eduscript.services.LogOutputProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class LogOutputProducerImpl implements LogOutputProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private final Logger logger = LoggerFactory.getLogger(LogOutputProducerImpl.class);

    @Value("${app.kafka.topics.logs}")
    private String logOutputTopic;

    public LogOutputProducerImpl(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void send(LogEntry logMsg) {
        try {
            if (logMsg.getJobId() == null)
                return;
                
            String logStr = objectMapper.writeValueAsString(logMsg);
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(
                    logOutputTopic, logMsg.getJobId().toString(), logStr);
            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    logger.error("An error occurred while sending data to topic", ex);
                } else {
                    logger.debug("Sent data to topic %s successfully: %s", logOutputTopic, logStr);
                }
            });
        } catch (JsonProcessingException ex) {
            logger.error("Could not serialize log entry: {}", ex);
        }
    }
}
