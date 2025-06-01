package org.eduscript.semantic.exceptions;

import org.eduscript.datastructures.Type;

public class TypeMismatchException extends SemanticException {
    
    private static final String TYPE = "TYPE MISMATCH ERROR";

    // Constructor for variable assignment type mismatch
    public TypeMismatchException(String variableName, Type expected, Type actual) {
        super(
            String.format("Type mismatch for variable '%s'", variableName),
            TYPE,
            String.format("Expected type: %s, but got: %s", expected, actual)
        );
    }
    
    // Constructor for variable assignment type mismatch with location
    public TypeMismatchException(String variableName, Type expected, Type actual, int line, int column) {
        super(
            String.format("Type mismatch for variable '%s'", variableName),
            TYPE,
            String.format("Expected type: %s, but got: %s", expected, actual),
            line,
            column
        );
    }
    
    // Constructor for operation type mismatch (uses boolean flag to differentiate)
    public TypeMismatchException(String operation, Type leftType, Type rightType, boolean isOperation) {
        super(
            String.format("Type mismatch in %s operation", operation),
            TYPE,
            String.format("Cannot perform %s between %s and %s", operation, leftType, rightType)
        );
    }
    
    // Constructor for operation type mismatch with location
    public TypeMismatchException(String operation, Type leftType, Type rightType, boolean isOperation, int line, int column) {
        super(
            String.format("Type mismatch in %s operation", operation),
            TYPE,
            String.format("Cannot perform %s between %s and %s", operation, leftType, rightType),
            line,
            column
        );
    }
    
    // Static factory methods for cleaner usage
    public static TypeMismatchException forVariable(String variableName, Type expected, Type actual) {
        return new TypeMismatchException(variableName, expected, actual);
    }
    
    public static TypeMismatchException forVariable(String variableName, Type expected, Type actual, int line, int column) {
        return new TypeMismatchException(variableName, expected, actual, line, column);
    }
    
    public static TypeMismatchException forOperation(String operation, Type leftType, Type rightType) {
        return new TypeMismatchException(operation, leftType, rightType, true);
    }
    
    public static TypeMismatchException forOperation(String operation, Type leftType, Type rightType, int line, int column) {
        return new TypeMismatchException(operation, leftType, rightType, true, line, column);
    }
}
