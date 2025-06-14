package org.eduscript.services.impl;

import java.util.concurrent.ExecutionException;

import org.eduscript.model.JobMessage;
import org.eduscript.services.JobRequestProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
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

        // CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(
        // jobRequestsTopic, job.getId().toString(), jobStr);
        // future.whenComplete((result, ex) -> {
        // if (ex != null) {
        // logger.error("An error occurred while sending data to topic", ex);
        // } else {
        // logger.debug("Sent data to topic %s successfully: %s", jobRequestsTopic,
        // jobStr);
        // }
        // });

        try {
            kafkaTemplate
                    .send(jobRequestsTopic, job.getId().toString(), jobStr)
                    .get(); // Blocks until send completes

            logger.debug("Sent data to topic {} successfully: {}", jobRequestsTopic, jobStr);
        } catch (InterruptedException e) {
            logger.error("Kafka send was interrupted", e);
            throw new RuntimeException("Kafka send interrupted", e);
        } catch (ExecutionException e) {
            logger.error("Failed to send data to topic", e.getCause());
            throw new RuntimeException("Kafka send failed", e.getCause());
        }

    }
}
