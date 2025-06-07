package org.eduscript.codegen;

import java.util.ArrayList;
import java.util.List;

public class BlockCommand extends BaseCommand {
    private List<BaseCommand> commands;
    private int indentLevel;

    public BlockCommand() {
        this.commands = new ArrayList<>();
        this.indentLevel = 1; // Default indentation for main block
    }

    public BlockCommand(int indentLevel) {
        this.commands = new ArrayList<>();
        this.indentLevel = indentLevel;
    }

    public void addCommand(BaseCommand command) {
        commands.add(command);
    }

    public List<BaseCommand> getCommands() {
        return commands;
    }

    public void setIndentLevel(int level) {
        this.indentLevel = level;
    }

    @Override
    public String generateCode() {
        StringBuilder code = new StringBuilder();
        String indent = "    ".repeat(indentLevel);
        
        for (BaseCommand cmd : commands) {
            String cmdCode = cmd.generateCode();
            // Add indentation to each line
            String[] lines = cmdCode.split("\n");
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    code.append(indent).append(line).append("\n");
                }
            }
        }
        
        return code.toString();
    }
}
