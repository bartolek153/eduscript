package org.eduscript.semantic.exceptions;

public class SymbolExistsScopeException extends SemanticException {
    
    private static final String TYPE = "DUPLICATE SYMBOL ERROR";

    public SymbolExistsScopeException(String symbolName) {
        super(
            String.format("Symbol '%s' already exists", symbolName),
            TYPE,
            String.format("Symbol '%s' is already defined in the current scope", symbolName)
        );
    }
    
    public SymbolExistsScopeException(String symbolName, int line, int column) {
        super(
            String.format("Symbol '%s' already exists", symbolName),
            TYPE,
            String.format("Symbol '%s' is already defined in the current scope", symbolName),
            line,
            column
        );
    }
    
    public SymbolExistsScopeException(String symbolName, String symbolType) {
        super(
            String.format("%s '%s' already exists", symbolType, symbolName),
            TYPE,
            String.format("%s '%s' is already defined in the current scope", symbolType, symbolName)
        );
    }
    
    public SymbolExistsScopeException(String symbolName, String symbolType, int line, int column) {
        super(
            String.format("%s '%s' already exists", symbolType, symbolName),
            TYPE,
            String.format("%s '%s' is already defined in the current scope", symbolType, symbolName),
            line,
            column
        );
    }
}
