package org.eduscript.services;

import org.eduscript.dto.JobDto;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface JobRequestProducer {
    void sendMessage(JobDto job) throws JsonProcessingException;
}
