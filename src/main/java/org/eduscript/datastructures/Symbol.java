package org.eduscript.datastructures;

/**
 * Represents a declared symbol (variable, constant, or function) in EduScript.
 */
public abstract class Symbol {
    private final String name;
    private final Type type;

    public Symbol(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    /**
     * Generates C code for this symbol declaration
     */
    public abstract String generateDeclaration();

    /**
     * Converts EduScript type to C type
     */
    protected String convertTypeToC(Type type) {
        switch (type) {
            case INTEIRO:
                return "int";
            case REAL:
                return "float";
            case LOGICO:
                return "bool";
            case CARACTERE:
                return "char";
            case CADEIA:
                return "char*";
            default:
                return "void";
        }
    }
}
