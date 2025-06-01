package org.eduscript.semantic;

import java.util.List;

public class FunctionSymbol extends Symbol {
    public final List<Symbol> parameters;
    public final Scope scope;

    public FunctionSymbol(String name, Type returnType, List<Symbol> parameters, Scope scope) {
        super(name, returnType);
        this.parameters = parameters;
        this.scope = scope;
    }
}
