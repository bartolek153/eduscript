package org.eduscript.logging;

import org.eduscript.model.LogEntry;

public interface LogHandler {
    void handle(LogEntry entry);
    void stop();
}
