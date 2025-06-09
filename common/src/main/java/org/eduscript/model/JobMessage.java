package org.eduscript.model;

import java.util.UUID;

public class JobMessage {
    private UUID id;
    private String sourceCode;

    public JobMessage() {
    }

    public JobMessage(UUID id, String sourceCode) {
        this.id = id;
        this.sourceCode = sourceCode;
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }
}
