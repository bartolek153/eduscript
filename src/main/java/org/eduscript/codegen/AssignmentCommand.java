package org.eduscript.codegen;

public class AssignmentCommand extends BaseCommand {
    private String variable;
    private String expression;
    private String arrayIndex; // For array assignments

    public AssignmentCommand(String variable, String expression) {
        this.variable = variable;
        this.expression = expression;
    }

    public AssignmentCommand(String variable, String arrayIndex, String expression) {
        this.variable = variable;
        this.arrayIndex = arrayIndex;
        this.expression = expression;
    }

    @Override
    public String generateCode() {
        StringBuilder code = new StringBuilder();
        code.append(variable);
        
        if (arrayIndex != null) {
            code.append("[").append(arrayIndex).append("]");
        }
        
        code.append(" = ").append(expression).append(";\n");
        return code.toString();
    }
}
