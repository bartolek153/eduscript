package org.eduscript.semantic.exceptions;

public class SymbolExistsScopeException extends SemanticException {
    public SymbolExistsScopeException() {
        super("symbol already defined in current scope");
    }

    public SymbolExistsScopeException(String msg) {
        super(msg);
    }
}
