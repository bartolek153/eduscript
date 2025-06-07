package org.eduscript.codegen;

import java.util.List;

public class WriteCommand extends BaseCommand {
    private List<String> expressions;

    public WriteCommand(List<String> expressions) {
        this.expressions = expressions;
    }

    @Override
    public String generateCode() {
        StringBuilder code = new StringBuilder();
        
        // Build a single printf statement for all expressions
        code.append("printf(");
        
        // Build format string
        StringBuilder format = new StringBuilder();
        StringBuilder args = new StringBuilder();
        
        for (int i = 0; i < expressions.size(); i++) {
            String expr = expressions.get(i);
            
            if (expr.startsWith("\"") && expr.endsWith("\"")) {
                // String literal - add its content to format string
                format.append(expr.substring(1, expr.length() - 1));
            } else {
                // Variable or expression - add format specifier
                // For simplicity, using %d for integers
                format.append("%d");
                if (args.length() > 0) {
                    args.append(", ");
                }
                args.append(expr);
            }
        }
        
        // Add newline at the end
        format.append("\\n");
        
        code.append("\"").append(format).append("\"");
        
        if (args.length() > 0) {
            code.append(", ").append(args);
        }
        
        code.append(");\n");
        
        return code.toString();
    }
}
