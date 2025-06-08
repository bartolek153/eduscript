package org.eduscript.services.impl;

import org.eduscript.dto.JobDto;
import org.eduscript.services.JobRequestProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class JobRequestProducerImpl implements JobRequestProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.jobs}")
    private String jobRequestsTopic;
    
    public JobRequestProducerImpl(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }
    
    @Override
    public void sendMessage(JobDto job) throws JsonProcessingException {
        String jobStr = objectMapper.writeValueAsString(job);
        kafkaTemplate.send(jobRequestsTopic, jobStr);
    }
}
