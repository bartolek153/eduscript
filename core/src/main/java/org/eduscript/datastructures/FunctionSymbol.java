package org.eduscript.datastructures;

import java.util.List;

public class FunctionSymbol extends Symbol {
    public final List<Symbol> parameters;
    public final Scope scope;
    private String body; // converted code body

    public FunctionSymbol(String name, EduType returnType, List<Symbol> parameters, Scope scope) {
        super(name, returnType);
        this.parameters = parameters;
        this.scope = scope;
    }
    
    public void setBody(String body) {
        this.body = body;
    }
    
    @Override
    public String generateDeclaration() {
        // define <return_type> @<function_name>(<arg_type> %<arg_name>, ...) {
        // entry:
        //   ; variable allocations
        //   ; instructions
        //   ; return
        // }
        
        StringBuilder code = new StringBuilder();
        
        code.append(String.format("define %s @%s(", 
            Symbol.getIrType(getType()), getName()));

        for (Symbol arg : parameters) {
            
        }
        
        return code.toString();
    }
}
