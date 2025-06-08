package org.eduscript.dto;

public class JobDto {
    private String id;
    private String sourceCode;

    public JobDto(String id, String sourceCode) {
        this.id = id;
        this.sourceCode = sourceCode;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }
}
