package org.eduscript.datastructures;

import java.util.List;

public class FunctionSymbol extends Symbol {
    public final List<Symbol> parameters;
    public final Scope scope;
    private String body; // C code body

    public FunctionSymbol(String name, Type returnType, List<Symbol> parameters, Scope scope) {
        super(name, returnType);
        this.parameters = parameters;
        this.scope = scope;
    }
    
    public void setBody(String body) {
        this.body = body;
    }
    
    @Override
    public String generateDeclaration() {
        StringBuilder code = new StringBuilder();
        
        // Function signature
        code.append(convertTypeToC(getType()));
        code.append(" ");
        code.append(getName());
        code.append("(");
        
        // Parameters
        for (int i = 0; i < parameters.size(); i++) {
            Symbol param = parameters.get(i);
            code.append(convertTypeToC(param.getType()));
            code.append(" ");
            code.append(param.getName());
            if (i < parameters.size() - 1) {
                code.append(", ");
            }
        }
        
        code.append(") {\n");
        
        // Function body
        if (body != null) {
            code.append(body);
        }
        
        code.append("}\n\n");
        return code.toString();
    }
}
