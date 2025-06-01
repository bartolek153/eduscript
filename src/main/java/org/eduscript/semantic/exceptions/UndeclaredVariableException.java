package org.eduscript.semantic.exceptions;

public class UndeclaredVariableException extends SemanticException {
    
    private static final String TYPE = "UNDECLARED VARIABLE ERROR";

    public UndeclaredVariableException(String variableName) {
        super(
            String.format("Undeclared variable '%s'", variableName),
            TYPE,
            String.format("Variable '%s' was not declared in the current scope", variableName)
        );
    }
    
    public UndeclaredVariableException(String variableName, int line, int column) {
        super(
            String.format("Undeclared variable '%s'", variableName),
            TYPE,
            String.format("Variable '%s' was not declared in the current scope", variableName),
            line,
            column
        );
    }
    
    public UndeclaredVariableException(String variableName, String scopeInfo) {
        super(
            String.format("Undeclared variable '%s'", variableName),
            TYPE,
            String.format("Variable '%s' was not declared. %s", variableName, scopeInfo)
        );
    }
    
    public UndeclaredVariableException(String variableName, String scopeInfo, int line, int column) {
        super(
            String.format("Undeclared variable '%s'", variableName),
            TYPE,
            String.format("Variable '%s' was not declared. %s", variableName, scopeInfo),
            line,
            column
        );
    }
}
