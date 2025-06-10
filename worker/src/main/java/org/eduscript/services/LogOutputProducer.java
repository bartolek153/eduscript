package org.eduscript.services;

import org.eduscript.model.LogEntry;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface LogOutputProducer {
    void send(LogEntry job) throws JsonProcessingException;
}
