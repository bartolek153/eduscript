package org.eduscript;

import static org.junit.Assert.*;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.eduscript.parser.SyntaxErrorListener;
import org.eduscript.semantic.SemanticAnalyzer;
import org.junit.*;

import main.antlr4.EduScriptLexer;
import main.antlr4.EduScriptParser;

public class EduScriptTest {
    
    private SemanticAnalyzer semantic = new SemanticAnalyzer();

    private ParseTree getParseTree(String source) {
        CharStream input = CharStreams.fromString(source);
        EduScriptLexer lexer = new EduScriptLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        EduScriptParser parser = new EduScriptParser(tokens);
        SyntaxErrorListener errorListener = new SyntaxErrorListener();
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);

        ParseTree tree = parser.program();

        if (parser.getNumberOfSyntaxErrors() > 0) {
            return null;
        }

        return tree;
    }

    @Test
    public void testGlobal() {
        String code = "programa a; inicio fimprograma";
        semantic.visit(getParseTree(code));
    }
}
