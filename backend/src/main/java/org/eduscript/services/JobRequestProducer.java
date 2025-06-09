package org.eduscript.services;

import org.eduscript.model.JobMessage;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface JobRequestProducer {
    void send(JobMessage job) throws JsonProcessingException;
}
