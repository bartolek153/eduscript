package org.eduscript.semantic;

public class ArraySymbol extends Symbol {
    private final int length;

    public ArraySymbol(String name, Type returnType, int length) {
        super(name, returnType);
        this.length = length;
    }

    public int getLength() {
        return length;
    }
}
