package org.eduscript.datastructures;

public class VariableSymbol extends Symbol {
    private String initialValue;
    private String label;

    public VariableSymbol(String name, EduType type) {
        super(name, type);
    }

    public void setInitialValue(String initialValue) {
        this.initialValue = initialValue;
    }

    public void setInitialValue(String initialValue, String label) {
        this.initialValue = initialValue;
        this.label = label;
    }

    @Override
    public String generateDeclaration() {
        StringBuilder code = new StringBuilder();
        EduType type = getType();
        String irtype = getIrType(type);

        code.append(String.format("%%%s = alloca %s\n", getName(), irtype));

        if (initialValue != null) {
            if (type == EduType.TEXTO && label != null) {
                int length = initialValue.length() + 1; // +1 for null terminator
                code.append(String.format(
                        "store %s getelementptr inbounds ([%d x i8], [%d x i8]* @%s, i32 0, i32 0), i8** %%%s\n",
                        irtype, length, length, label, getName()));
            } else {
                code.append(String.format("store %s %s, %s* %%%s\n",
                        irtype, initialValue, irtype, getName()));
            }
        }
        return code.toString();
    }

    public String generateGlobalString() {
        int length = initialValue.length() + 1; // +1 for null terminator
        String escaped = initialValue.replace("\\", "\\5C").replace("\"", "\\22"); // escape for LLVM
        return String.format("@%s = private unnamed_addr constant [%d x i8] c\"%s\\00\", align 1",
                label, length, escaped);
    }
}
