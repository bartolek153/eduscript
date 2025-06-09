package org.eduscript.model;

import java.util.UUID;

public class CompileResponse {
    private UUID jobId;

    public CompileResponse() {}

    public CompileResponse(JobMessage job) {
        this.jobId = job.getId();
    }
    
    public UUID getJobId() {
        return jobId;
    }

    public void setJobId(UUID jobId) {
        this.jobId = jobId;
    }
}
