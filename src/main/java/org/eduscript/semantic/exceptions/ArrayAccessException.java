package org.eduscript.semantic.exceptions;

public class ArrayAccessException extends SemanticException {
    
    private static final String TYPE = "ARRAY ACCESS ERROR";

    public ArrayAccessException(String variableName) {
        super(
            String.format("Invalid array access on variable '%s'", variableName),
            TYPE,
            String.format("Variable '%s' was used as an array but is not declared as one", variableName)
        );
    }
    
    public ArrayAccessException(String variableName, int line, int column) {
        super(
            String.format("Invalid array access on variable '%s'", variableName),
            TYPE,
            String.format("Variable '%s' was used as an array but is not declared as one", variableName),
            line,
            column
        );
    }
    
    public ArrayAccessException(String variableName, String details) {
        super(
            String.format("Invalid array access on variable '%s'", variableName),
            TYPE,
            details
        );
    }
    
    public ArrayAccessException(String variableName, String details, int line, int column) {
        super(
            String.format("Invalid array access on variable '%s'", variableName),
            TYPE,
            details,
            line,
            column
        );
    }
}
