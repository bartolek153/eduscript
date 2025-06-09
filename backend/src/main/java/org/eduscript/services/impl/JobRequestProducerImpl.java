package org.eduscript.services.impl;

import java.util.concurrent.CompletableFuture;

import org.eduscript.model.JobMessage;
import org.eduscript.services.JobRequestProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class JobRequestProducerImpl implements JobRequestProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private final Logger logger = LoggerFactory.getLogger(JobRequestProducerImpl.class);

    @Value("${app.kafka.topics.jobs}")
    private String jobRequestsTopic;

    public JobRequestProducerImpl(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void send(JobMessage job) throws JsonProcessingException {
        String jobStr = objectMapper.writeValueAsString(job);
        
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(
                jobRequestsTopic, job.getId().toString(), jobStr);
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                logger.error("An error occurred while sending data to topic", ex);
            } else {
                logger.debug("Sent data to topic %s successfully: %s", jobRequestsTopic, jobStr);
            }
        });
    }
}
