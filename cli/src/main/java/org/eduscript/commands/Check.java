package org.eduscript.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.eduscript.logging.Logger;
import org.eduscript.parser.SyntaxErrorListener;
import org.eduscript.semantic.SemanticAnalyzer;

import main.antlr4.EduScriptLexer;
import main.antlr4.EduScriptParser;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "check", 
         description = "Check EduScript source files for syntax and semantic errors without compilation",
         mixinStandardHelpOptions = true)
public class Check implements Callable<Integer> {

    @Parameters(index = "0", 
                description = "Input EduScript source file (.edu)")
    private String inputFile;

    @Option(names = {"-v", "--verbose"}, 
            description = "Enable verbose output")
    private boolean verbose = false;

    @Option(names = {"--syntax-only"}, 
            description = "Only perform syntax checking")
    private boolean syntaxOnly = false;

    @Override
    public Integer call() throws Exception {
        long startTime = System.currentTimeMillis();

        // Validate input file
        Path inputPath = Paths.get(inputFile);
        if (!Files.exists(inputPath)) {
            System.err.println("Error: Input file '" + inputFile + "' does not exist.");
            return 1;
        }

        if (!inputFile.endsWith(".edu")) {
            System.err.println("Warning: Input file should have .edu extension.");
        }

        // Read source file
        String source;
        try {
            Logger.printFileInfo("Checking source file", inputFile);
            source = Files.readString(inputPath);
            if (verbose) {
                Logger.printStats("File size", Files.size(inputPath) + " bytes");
            }
        } catch (IOException e) {
            Logger.printError("Failed to read source file: " + e.getMessage());
            return 1;
        }

        // Phase 1: Lexical Analysis
        Logger.printSeparator();
        Logger.printPhase("Starting syntax check");
        
        CharStream input = CharStreams.fromString(source);
        EduScriptLexer lexer = new EduScriptLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        
        // Phase 2: Syntax Analysis
        EduScriptParser parser = new EduScriptParser(tokens);
        SyntaxErrorListener errorListener = new SyntaxErrorListener();
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);
        
        ParseTree tree = parser.program();
        
        if (errorListener.hasError()) {
            Logger.printError("Syntax check failed");
            Logger.printCompilationResult(false, System.currentTimeMillis() - startTime);
            return 1;
        }
        
        Logger.printSuccess("Syntax check passed");
        
        if (syntaxOnly) {
            Logger.printInfo("Syntax-only mode: stopping after syntax check");
            Logger.printCompilationResult(true, System.currentTimeMillis() - startTime);
            return 0;
        }

        // Phase 3: Semantic Analysis
        Logger.printPhase("Starting semantic check");
        
        SemanticAnalyzer semantic = new SemanticAnalyzer();
        semantic.visit(tree);
        
        if (semantic.getErrorHandler().hasErrors()) {
            semantic.getErrorHandler().printSummary();
            Logger.printError("Semantic check failed");
            Logger.printCompilationResult(false, System.currentTimeMillis() - startTime);
            return 1;
        }
        
        Logger.printSuccess("Semantic check passed");
        Logger.printInfo("All checks passed successfully!");
        Logger.printCompilationResult(true, System.currentTimeMillis() - startTime);
        return 0;
    }
}
