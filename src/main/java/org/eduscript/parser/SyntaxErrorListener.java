package org.eduscript.parser;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class SyntaxErrorListener extends BaseErrorListener {
    private boolean hasError = false;

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
            Object offendingSymbol,
            int line, int charPositionInLine,
            String msg,
            RecognitionException e) {
        hasError = true;
        System.err.printf("Syntax error at line %d:%d - %s%n", line, charPositionInLine, msg);
    }

    public boolean hasError() {
        return hasError;
    }
}
