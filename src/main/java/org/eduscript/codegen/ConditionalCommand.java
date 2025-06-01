package org.eduscript.codegen;

public class ConditionalCommand extends BaseCommand {
    private String condition;
    private BlockCommand thenBlock;
    private BlockCommand elseBlock;

    public ConditionalCommand(String condition, BlockCommand thenBlock) {
        this.condition = condition;
        this.thenBlock = thenBlock;
    }

    public void setElseBlock(BlockCommand elseBlock) {
        this.elseBlock = elseBlock;
    }

    @Override
    public String generateCode() {
        StringBuilder code = new StringBuilder();
        
        code.append("if (").append(condition).append(") {\n");
        if (thenBlock != null) {
            code.append(thenBlock.generateCode());
        }
        code.append("}");
        
        if (elseBlock != null) {
            code.append(" else {\n");
            code.append(elseBlock.generateCode());
            code.append("}");
        }
        
        code.append("\n");
        return code.toString();
    }
}
