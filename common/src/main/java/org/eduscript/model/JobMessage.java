package org.eduscript.model;

import java.util.List;
import java.util.UUID;

public class JobMessage {
    private UUID id;
    private List<JobTask> tasks;

    public JobMessage(UUID id, List<JobTask> tasks) {
        this.id = id;
        this.tasks = tasks;
    }

    public JobMessage() {
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<JobTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<JobTask> tasks) {
        this.tasks = tasks;
    }
}
