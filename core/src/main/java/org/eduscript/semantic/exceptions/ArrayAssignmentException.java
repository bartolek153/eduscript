package org.eduscript.semantic.exceptions;

public class ArrayAssignmentException extends SemanticException {
    
    private static final String TYPE = "ARRAY ASSIGNMENT ERROR";

    public ArrayAssignmentException(String arrayName) {
        super(
            String.format("Invalid array assignment to '%s'", arrayName),
            TYPE,
            "Array assignments must provide both an index and a value"
        );
    }
    
    public ArrayAssignmentException(String arrayName, int line, int column) {
        super(
            String.format("Invalid array assignment to '%s'", arrayName),
            TYPE,
            "Array assignments must provide both an index and a value",
            line,
            column
        );
    }
    
    public ArrayAssignmentException(String arrayName, String details) {
        super(
            String.format("Invalid array assignment to '%s'", arrayName),
            TYPE,
            details
        );
    }
    
    public ArrayAssignmentException(String arrayName, String details, int line, int column) {
        super(
            String.format("Invalid array assignment to '%s'", arrayName),
            TYPE,
            details,
            line,
            column
        );
    }
}
