package org.eduscript.services;

import org.eduscript.model.JobMessage;

public interface SandboxExecutor {
    void execute(JobMessage job);
}
