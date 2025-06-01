package org.eduscript.codegen;

import java.util.ArrayList;
import java.util.List;

public class ProgramCommand extends BaseCommand {
    private String programName;
    private List<BaseCommand> globalDeclarations;
    private BlockCommand mainBlock;

    public ProgramCommand(String programName) {
        this.programName = programName;
        this.globalDeclarations = new ArrayList<>();
    }

    public void addGlobalDeclaration(BaseCommand declaration) {
        globalDeclarations.add(declaration);
    }

    public void setMainBlock(BlockCommand mainBlock) {
        this.mainBlock = mainBlock;
    }

    @Override
    public String generateCode() {
        StringBuilder code = new StringBuilder();
        
        // C headers
        code.append("#include <stdio.h>\n");
        code.append("#include <stdlib.h>\n");
        code.append("#include <stdbool.h>\n");
        code.append("#include <string.h>\n\n");
        
        // Global declarations
        for (BaseCommand decl : globalDeclarations) {
            code.append(decl.generateCode());
        }
        
        // Main function
        code.append("\nint main() {\n");
        if (mainBlock != null) {
            code.append(mainBlock.generateCode());
        }
        code.append("    return 0;\n");
        code.append("}\n");
        
        return code.toString();
    }
}
