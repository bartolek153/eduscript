package org.eduscript.codegen;

public class WhileCommand extends BaseCommand {
    private String condition;
    private BlockCommand body;

    public WhileCommand(String condition, BlockCommand body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public String generateCode() {
        StringBuilder code = new StringBuilder();
        
        code.append("while (").append(condition).append(") {\n");
        if (body != null) {
            code.append(body.generateCode());
        }
        code.append("}\n");
        
        return code.toString();
    }
}
