package org.eduscript.services.impl;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.eduscript.codegen.CodeGenerator;
import org.eduscript.logging.Logger;
import org.eduscript.model.JobMessage;
import org.eduscript.parser.SyntaxErrorListener;
import org.eduscript.semantic.SemanticAnalyzer;
import org.eduscript.services.CompileService;
import org.springframework.stereotype.Service;

import main.antlr4.EduScriptLexer;
import main.antlr4.EduScriptParser;

@Service
public class CompileServiceImpl implements CompileService {

    @Override
    public void compile(JobMessage job) {
        long startTime = System.currentTimeMillis();
        String source;
        String id = job.getId().toString();

        Logger.printHeader("EduScript Compiler");
        Logger.printInfo("Starting compilation process...", id);

        source = """
                programa SimpleTest;

                    var x: inteiro;
                    var y: inteiro;

                    inicio
                        x = 10;
                        y = 20;
                        escrever("Hello from EduScript!");
                        escrever("x = ", x);
                        escrever("y = ", y);
                        escrever("x + y = ", x + y);
                    fimprograma

                """;

        Logger.printSeparator();
        Logger.printPhase("starting lexical analysis");

        Logger.printStep("transforming source code in CharStream", id);
        CharStream input = CharStreams.fromString(source);

        Logger.printStep("creating lexer", id);
        EduScriptLexer lexer = new EduScriptLexer(input);

        Logger.printStep("reading tokens");
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Logger.printSuccess("finished lexical analysis", id);

        Logger.printPhase("starting syntax analysis");

        Logger.printStep("creating parser", id);
        EduScriptParser parser = new EduScriptParser(tokens);

        Logger.printStep("adding custom syntax error listener", id);
        SyntaxErrorListener errorListener = new SyntaxErrorListener();
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);

        Logger.printStep("parsing source code", id);
        ParseTree tree = parser.program();

        if (errorListener.hasError()) {
            Logger.printError("syntax analysis failed", id);
            Logger.printCompilationResult(false, System.currentTimeMillis() - startTime);
            System.exit(1);
            return;
        }

        Logger.printSuccess("syntax analysis completed successfully", id);
        Logger.printPhase("starting semantic analysis");

        Logger.printStep("creating semantic analyzer", id);
        SemanticAnalyzer semantic = new SemanticAnalyzer();
        semantic.visit(tree);

        if (semantic.getErrorHandler().hasErrors()) {
            semantic.getErrorHandler().printSummary();
            Logger.printError("compilation failed due to semantic errors.", id);
            System.exit(1);
            return;
        }

        Logger.printSuccess("semantic analysis completed successfully", id);
        Logger.printPhase("starting code generation");

        Logger.printStep("creating generator", id);
        CodeGenerator codeGen = new CodeGenerator();
        codeGen.visit(tree);

        String generatedCode = codeGen.generateCode();
        Logger.printExtern(generatedCode, id);
    }
}
