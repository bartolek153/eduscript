package org.eduscript.codegen;

import java.util.List;

public class ReadCommand extends BaseCommand {
    private List<String> variables;

    public ReadCommand(List<String> variables) {
        this.variables = variables;
    }

    @Override
    public String generateCode() {
        StringBuilder code = new StringBuilder();
        
        for (String var : variables) {
            // For simplicity, assuming integer input
            // In a real implementation, you'd need type information
            code.append("scanf(\"%d\", &");
            code.append(var);
            code.append(");\n");
        }
        
        return code.toString();
    }
}
