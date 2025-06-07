package org.eduscript.datastructures;

/**
 * Represents a declared symbol (variable, constant, or function) in EduScript.
 */
public abstract class Symbol {
    private final String name;
    private final EduType type;

    public Symbol(String name, EduType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public EduType getType() {
        return type;
    }

    public abstract String generateDeclaration();

    /**
     * Converts EduScript type to C type
     */
    protected String convertTypeToC(EduType type) {
        switch (type) {
            case INTEIRO:
                return "int";
            case REAL:
                return "float";
            case LOGICO:
                return "bool";
            case CARACTERE:
                return "char";
            case TEXTO:
                return "char*";
            default:
                return "void";
        }
    }

    public static String getIrType(EduType type) {
        switch (type) {
            case LOGICO:
                return "i1"; // 1 bit
            case CARACTERE:
                return "i8"; // 8 bits = 1 byte
            case INTEIRO:
                return "i32"; // 32 bits
            case REAL:
                return "double"; // 64 bits
            case TEXTO:
                return "i8*"; // char array pointer
            default:  //TODO: create unsupported type exception
                throw new IllegalArgumentException("Tipo não suportado: " + type);
        }
    }
}
