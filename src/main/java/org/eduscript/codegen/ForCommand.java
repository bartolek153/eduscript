package org.eduscript.codegen;

public class ForCommand extends BaseCommand {
    private String variable;
    private String startValue;
    private String endValue;
    private String step;
    private BlockCommand body;

    public ForCommand(String variable, String startValue, String endValue, BlockCommand body) {
        this.variable = variable;
        this.startValue = startValue;
        this.endValue = endValue;
        this.step = "1"; // Default step
        this.body = body;
    }

    public void setStep(String step) {
        this.step = step;
    }

    @Override
    public String generateCode() {
        StringBuilder code = new StringBuilder();
        
        // Generate C-style for loop
        code.append("for (int ").append(variable).append(" = ").append(startValue);
        code.append("; ").append(variable).append(" <= ").append(endValue);
        code.append("; ").append(variable).append(" += ").append(step);
        code.append(") {\n");
        
        if (body != null) {
            code.append(body.generateCode());
        }
        
        code.append("}\n");
        
        return code.toString();
    }
}
