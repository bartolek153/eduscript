package org.eduscript.model;

import java.util.UUID;

import org.eduscript.enums.JobStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "job_sessions", timeToLive = 3600)
public class JobMetadata {
    @Id
    private UUID jobId;
    private UUID userId;
    private JobStatus status;

    public JobMetadata() {
    }

    public JobMetadata(UUID jobId, UUID userId, JobStatus status) {
        this.jobId = jobId;
        this.userId = userId;
        this.status = status;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getJobId() {
        return jobId;
    }

    public void setJobId(UUID jobId) {
        this.jobId = jobId;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }
}
