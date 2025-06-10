package org.eduscript.services;

import org.eduscript.model.JobMessage;

public interface CompileService {
    void compile(JobMessage job);
}
