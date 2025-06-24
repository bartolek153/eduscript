package org.eduscript.services;

import java.util.UUID;

import org.eduscript.model.JobMessage;

public interface SandboxJobExecutor {
    Boolean execute(JobMessage job);

    Boolean cancel(UUID jobId);
}
