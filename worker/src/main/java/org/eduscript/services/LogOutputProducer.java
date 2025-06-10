package org.eduscript.services;

import org.eduscript.model.LogEntry;

public interface LogOutputProducer {
    void send(LogEntry job);
}
