package org.eduscript.semantic;

import java.util.ArrayList;
import java.util.List;

public class SemanticErrorHandler {
    private List<SemanticError> errors = new ArrayList<>();
    
    public static class SemanticError {
        private final String message;
        private final String context;
        private final int line;
        private final int column;
        
        public SemanticError(String message, String context, int line, int column) {
            this.message = message;
            this.context = context;
            this.line = line;
            this.column = column;
        }
        
        public SemanticError(String message, String context) {
            this(message, context, -1, -1);
        }
        
        public String getMessage() { return message; }
        public String getContext() { return context; }
        public int getLine() { return line; }
        public int getColumn() { return column; }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("═══════════════════════════════════════════════════════════════\n");
            sb.append("                       SEMANTIC ERROR                          \n");
            sb.append("═══════════════════════════════════════════════════════════════\n");
            
            if (line > 0 && column > 0) {
                sb.append(String.format("Location: Line %d, Column %d\n", line, column));
            }
            
            if (context != null && !context.isEmpty()) {
                sb.append(String.format("Context: %s\n", context));
            }
            
            sb.append(String.format("Error: %s\n", message));
            sb.append("───────────────────────────────────────────────────────────────\n");
            
            return sb.toString();
        }
    }
    
    public void reportError(String message, String context, int line, int column) {
        errors.add(new SemanticError(message, context, line, column));
    }
    
    public void reportError(String message, String context) {
        errors.add(new SemanticError(message, context));
    }
    
    public void reportError(String message) {
        errors.add(new SemanticError(message, ""));
    }
    
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    public List<SemanticError> getErrors() {
        return new ArrayList<>(errors);
    }
    
    public int getErrorCount() {
        return errors.size();
    }
    
    public void printSummary() {
        if (!hasErrors()) {
            return;
        }
        
        System.err.println("\n" + "═".repeat(65));
        System.err.println("                    SEMANTIC ANALYSIS FAILED                   ");
        System.err.println("═".repeat(65));
        System.err.printf("Found %d semantic error%s:\n\n", errors.size(), errors.size() == 1 ? "" : "s");
        
        for (int i = 0; i < errors.size(); i++) {
            System.err.printf("Error %d of %d:\n", i + 1, errors.size());
            System.err.println(errors.get(i).toString());
        }
        
        System.err.println("═".repeat(65));
        System.err.println("Compilation stopped due to semantic errors.");
        System.err.println("═".repeat(65));
    }
    
    public void clear() {
        errors.clear();
    }
}
