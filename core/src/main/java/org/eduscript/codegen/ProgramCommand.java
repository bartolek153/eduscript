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
        
        // Global declarations
        for (BaseCommand decl : globalDeclarations) {
            String b = decl.generateCode();
            code.append(b);
        }
        
        // Main function
        code.append("define i32 @main() {\n");
        code.append("entry:\n");
        String b = code.toString();
        if (mainBlock != null) {
            code.append(mainBlock.generateCode());
        }
        code.append("  ret i32 0\n");
        code.append("}\n");
        
        String a =  code.toString();
        return a;
    }

    public String getProgramName() {
        return this.programName;
    }
}
