package org.eduscript.semantic;

import java.util.ArrayList;
import java.util.List;

import org.eduscript.semantic.exceptions.SemanticException;

public class SemanticErrorHandler {
    private List<SemanticException> errors;
    private boolean hasErrors;
    
    public SemanticErrorHandler() {
        this.errors = new ArrayList<>();
        this.hasErrors = false;
    }
    
    public void reportError(SemanticException error) {
        errors.add(error);
        hasErrors = true;
        
        // Print the pretty formatted error immediately
        System.err.print(error.getPrettyErrorMessage());
    }
    
    public boolean hasErrors() {
        return hasErrors;
    }
    
    public List<SemanticException> getErrors() {
        return new ArrayList<>(errors);
    }
    
    public int getErrorCount() {
        return errors.size();
    }
    
    public void printSummary() {
        if (hasErrors) {
            System.err.println("\n" + "═".repeat(63));
            System.err.println("                    SEMANTIC ANALYSIS FAILED");
            System.err.println("═".repeat(63));
            System.err.printf("Total semantic errors found: %d\n", errors.size());
            System.err.println("═".repeat(63));
        } else {
            System.out.println("✓ Semantic analysis completed successfully.");
        }
    }
    
    public void reset() {
        errors.clear();
        hasErrors = false;
    }
}
