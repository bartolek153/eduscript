package org.eduscript.datastructures;

import java.util.*;

import org.eduscript.semantic.exceptions.SemanticException;
import org.eduscript.semantic.exceptions.SymbolExistsScopeException;

/**
 * Represents a scope in the EduScript program
 */
public class Scope {
    private final Map<String, Symbol> symbols = new HashMap<>();
    private final Scope parent;

    public Scope(Scope parent) {
        this.parent = parent;
    }

    public void define(Symbol sym) throws SemanticException {
        if (symbols.containsKey(sym.getName())) {
            throw new SymbolExistsScopeException(sym.getName());
        }
        symbols.put(sym.getName(), sym);
    }

    public Symbol resolve(String name) {
        Symbol sym = symbols.get(name);
        if (sym != null)
            return sym;
        if (parent != null)
            return parent.resolve(name);
        return null;
    }

    public Scope getParent() {
        return parent;
    }
}
