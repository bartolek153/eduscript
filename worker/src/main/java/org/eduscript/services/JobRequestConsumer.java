package org.eduscript.services;

import org.eduscript.model.JobMessage;

public interface JobRequestConsumer {
    void listen(JobMessage job);
}
