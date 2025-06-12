package org.eduscript.services;

import org.eduscript.model.LogEntry;

public interface LogOutputConsumer {
    void consume(LogEntry log);
}
