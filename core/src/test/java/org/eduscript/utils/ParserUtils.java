package org.eduscript.utils;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.eduscript.parser.SyntaxErrorListener;

import main.antlr4.EduScriptLexer;
import main.antlr4.EduScriptParser;

public class ParserUtils {
    public static EduScriptParser getParser(String source) {
        CharStream input = CharStreams.fromString(source);
        EduScriptLexer lexer = new EduScriptLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        EduScriptParser parser = new EduScriptParser(tokens);
        SyntaxErrorListener errorListener = new SyntaxErrorListener();
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);

        return parser;
    }
    
    public static ParseTree getParseTree(String source) {
        EduScriptParser parser = ParserUtils.getParser(source);

        ParseTree tree = parser.pipeline();

        if (parser.getNumberOfSyntaxErrors() > 0) {
            return null;
        }

        return tree;
    }
}
