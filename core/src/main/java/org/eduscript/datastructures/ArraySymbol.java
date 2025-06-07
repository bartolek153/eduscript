package org.eduscript.datastructures;

public class ArraySymbol extends Symbol {
    private final int length;

    public ArraySymbol(String name, EduType returnType, int length) {
        super(name, returnType);
        this.length = length;
    }

    public int getLength() {
        return length;
    }
    
    @Override
    public String generateDeclaration() {
        StringBuilder code = new StringBuilder();
        code.append(convertTypeToC(getType()));
        code.append(" ");
        code.append(getName());
        code.append("[");
        code.append(length);
        code.append("];");
        code.append("\n");
        return code.toString();
    }
}
