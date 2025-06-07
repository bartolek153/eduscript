package org.eduscript.semantic.exceptions;

public abstract class SemanticException extends Exception {
    private int line;
    private int column;
    private String errorType;
    private String details;
    
    public SemanticException(String message) {
        super(message);
        this.errorType = "SEMANTIC ERROR";
    }
    
    public SemanticException(String message, String errorType) {
        super(message);
        this.errorType = errorType;
    }
    
    public SemanticException(String message, String errorType, int line, int column) {
        super(message);
        this.errorType = errorType;
        this.line = line;
        this.column = column;
    }
    
    public SemanticException(String message, String errorType, String details) {
        super(message);
        this.errorType = errorType;
        this.details = details;
    }
    
    public SemanticException(String message, String errorType, String details, int line, int column) {
        super(message);
        this.errorType = errorType;
        this.details = details;
        this.line = line;
        this.column = column;
    }
    
    public String getPrettyErrorMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("═══════════════════════════════════════════════════════════════\n");
        sb.append(String.format("                    %s                    \n", errorType));
        sb.append("═══════════════════════════════════════════════════════════════\n");
        
        if (line > 0 && column > 0) {
            sb.append(String.format("Location: Line %d, Column %d\n", line, column));
        }
        
        sb.append(String.format("Error: %s\n", getMessage()));
        
        if (details != null && !details.isEmpty()) {
            sb.append(String.format("Details: %s\n", details));
        }
        
        return sb.toString();
    }
    
    public int getLine() {
        return line;
    }
    
    public int getColumn() {
        return column;
    }
    
    public String getErrorType() {
        return errorType;
    }
    
    public String getDetails() {
        return details;
    }
}
