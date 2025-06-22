package org.eduscript;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.eduscript.codegen.CodeGenerator;
import org.eduscript.exec.CRunner;
import org.eduscript.exec.Runner;
import org.eduscript.logging.Logger;
import org.eduscript.parser.SyntaxErrorListener;
import org.eduscript.semantic.SemanticAnalyzer;

import main.antlr4.EduScriptLexer;
import main.antlr4.EduScriptParser;

public class App
{
    public static void main( String[] args ) {
        long startTime = System.currentTimeMillis();
        String source;
        String outputFile = "output/";
        boolean success = true;

        Logger.printHeader("EduScript Compiler");
        Logger.printInfo("Starting compilation process...");
        
        if (args.length > 0 && !args[0].startsWith("-D")) {
            // Read from file
            Logger.printFileInfo("Reading source file", args[0]);
            try {
                source = new String(Files.readAllBytes(Paths.get(args[0])));
                Logger.printSuccess("Source file loaded successfully");
                Logger.printStats("File size", Files.size(Paths.get(args[0])) + " bytes");
                if (args.length > 1) {
                    outputFile = args[1];
                    Logger.printInfo("Output directory set to: " + outputFile);
                }
            } catch (IOException e) {
                Logger.printError("Failed to read source file: " + e.getMessage());
                return;
            }
        } else {
            Logger.printInfo("Using default test program");
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
        }

        Logger.printSeparator();
        Logger.printPhase("starting lexical analysis");
        
        Logger.printStep("transforming source code in CharStream");
        CharStream input = CharStreams.fromString(source);

        Logger.printStep("creating lexer");
        EduScriptLexer lexer = new EduScriptLexer(input);

        Logger.printStep("reading tokens");
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        // Logger.printStats("Tokens generated", String.valueOf(tokens.size()));
        Logger.printSuccess("finished lexical analysis");

        Logger.printPhase("starting syntax analysis");

        Logger.printStep("creating parser");
        EduScriptParser parser = new EduScriptParser(tokens);

        Logger.printStep("adding custom syntax error listener");
        SyntaxErrorListener errorListener = new SyntaxErrorListener();
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);

        Logger.printStep("parsing source code");
        ParseTree tree = parser.pipeline();

        if (errorListener.hasError()) {
            Logger.printError("syntax analysis failed");
            Logger.printCompilationResult(false, System.currentTimeMillis() - startTime);
            System.exit(1);
            return;
        }
        
        Logger.printSuccess("syntax analysis completed successfully");
        Logger.printPhase("starting semantic analysis");

        Logger.printStep("creating semantic analyzer");
        SemanticAnalyzer semantic = new SemanticAnalyzer();
        semantic.visit(tree);
        
        if (semantic.getErrorHandler().hasErrors()) {
            semantic.getErrorHandler().printSummary();
            Logger.printError("compilation failed due to semantic errors.");
            System.exit(1);
            return;
        }
    
        Logger.printSuccess("semantic analysis completed successfully");
        Logger.printPhase("starting code generation");

        Logger.printStep("creating generator");
        CodeGenerator codeGen = new CodeGenerator();
        codeGen.visit(tree);

        outputFile += codeGen.getProgram().getProgramName() + ".c";
        String generatedCode = codeGen.generateCode();
        
        try {
            Logger.printStep("writing code to temporary file in '" + outputFile + "'");
            Files.write(Paths.get(outputFile), generatedCode.getBytes());
        } catch (IOException e) {
            Logger.printError("Error writing output file: " + e.getMessage());
        }

        Logger.printSuccess("code generated succesfully");

        Logger.printPhase("invoking code runner");
        Logger.printStep("creating c runner");
        Runner runner = new CRunner();
        runner.invoke(outputFile);
    }
}
