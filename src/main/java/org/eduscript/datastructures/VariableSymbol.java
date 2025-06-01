package org.eduscript.datastructures;

public class VariableSymbol extends Symbol {
    private String initialValue;
    
    public VariableSymbol(String name, Type type) {
        super(name, type);
    }
    
    public void setInitialValue(String initialValue) {
        this.initialValue = initialValue;
    }
    
    @Override
    public String generateDeclaration() {
        StringBuilder code = new StringBuilder();
        code.append(convertTypeToC(getType()));
        code.append(" ");
        code.append(getName());
        
        if (initialValue != null) {
            code.append(" = ").append(initialValue);
        }
        
        code.append(";\n");
        return code.toString();
    }
}
