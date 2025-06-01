package org.eduscript.semantic.exceptions;

import org.eduscript.semantic.SemanticException;

public class UndeclaredVariableException extends SemanticException {
    public UndeclaredVariableException(String vbl) {
        super(String.format("Variable %s was not declared", vbl));
    }
}
