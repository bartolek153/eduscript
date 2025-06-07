package org.eduscript.parser;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

public class SyntaxErrorListener extends BaseErrorListener {
    private boolean hasError = false;
    private StringBuilder errorMessages = new StringBuilder();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
            Object offendingSymbol,
            int line, int charPositionInLine,
            String msg,
            RecognitionException e) {
        hasError = true;
        
        // Pretty format the error message
        errorMessages.append("\n");
        errorMessages.append("═══════════════════════════════════════════════════════════════\n");
        errorMessages.append("                        SYNTAX ERROR                           \n");
        errorMessages.append("═══════════════════════════════════════════════════════════════\n");
        errorMessages.append(String.format("Location: Line %d, Column %d\n", line, charPositionInLine + 1));
        
        if (offendingSymbol instanceof Token) {
            Token token = (Token) offendingSymbol;
            errorMessages.append(String.format("Unexpected token: '%s'\n", token.getText()));
        }
        
        errorMessages.append(String.format("Error: %s\n", msg));
        errorMessages.append("───────────────────────────────────────────────────────────────\n");
        errorMessages.append("Compilation stopped due to syntax error.\n");
        errorMessages.append("═══════════════════════════════════════════════════════════════\n");
        
        // Print immediately for immediate feedback
        System.err.print(errorMessages.toString());
    }

    public boolean hasError() {
        return hasError;
    }
    
    public String getErrorMessages() {
        return errorMessages.toString();
    }
}
