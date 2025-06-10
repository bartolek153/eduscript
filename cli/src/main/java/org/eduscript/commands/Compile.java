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
import org.eduscript.codegen.CodeGenerator;
import org.eduscript.exec.CRunner;
import org.eduscript.exec.Runner;
import org.eduscript.logging.Logger;
import org.eduscript.parser.SyntaxErrorListener;
import org.eduscript.semantic.SemanticAnalyzer;

import main.antlr4.EduScriptLexer;
import main.antlr4.EduScriptParser;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "compile", 
         description = "Compile EduScript source files to C and optionally execute them",
         mixinStandardHelpOptions = true)
public class Compile implements Callable<Integer> {

    @Parameters(index = "0", 
                description = "Input EduScript source file (.edu)")
    private String inputFile;

    @Option(names = {"-o", "--output"}, 
            description = "Output directory for generated C file (default: output/)")
    private String outputDir = "output/";

    @Option(names = {"-r", "--run"}, 
            description = "Compile and run the generated executable")
    private boolean runAfterCompile = false;

    @Option(names = {"-k", "--keep-c"}, 
            description = "Keep the generated C file after compilation")
    private boolean keepCFile = false;

    @Option(names = {"-v", "--verbose"}, 
            description = "Enable verbose output")
    private boolean verbose = false;

    @Option(names = {"--no-colors"}, 
            description = "Disable colored output")
    private boolean noColors = false;

    @Option(names = {"--syntax-only"}, 
            description = "Only perform syntax analysis (no semantic analysis or code generation)")
    private boolean syntaxOnly = false;

    @Option(names = {"--semantic-only"}, 
            description = "Perform syntax and semantic analysis only (no code generation)")
    private boolean semanticOnly = false;

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

        // Create output directory if it doesn't exist
        Path outputPath = Paths.get(outputDir);
        try {
            Files.createDirectories(outputPath);
        } catch (IOException e) {
            System.err.println("Error: Could not create output directory: " + e.getMessage());
            return 1;
        }

        // Read source file
        String source;
        try {
            Logger.printFileInfo("Reading source file", inputFile);
            source = Files.readString(inputPath);
            Logger.printSuccess("Source file loaded successfully");
            if (verbose) {
                Logger.printStats("File size", Files.size(inputPath) + " bytes");
            }
        } catch (IOException e) {
            Logger.printError("Failed to read source file: " + e.getMessage());
            return 1;
        }

        // Phase 1: Lexical Analysis
        Logger.printSeparator();
        Logger.printPhase("Starting lexical analysis");
        
        CharStream input = CharStreams.fromString(source);
        EduScriptLexer lexer = new EduScriptLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        
        Logger.printSuccess("Lexical analysis completed successfully");

        // Phase 2: Syntax Analysis
        Logger.printPhase("Starting syntax analysis");
        
        EduScriptParser parser = new EduScriptParser(tokens);
        SyntaxErrorListener errorListener = new SyntaxErrorListener();
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);
        
        ParseTree tree = parser.program();
        
        if (errorListener.hasError()) {
            Logger.printError("Syntax analysis failed");
            Logger.printCompilationResult(false, System.currentTimeMillis() - startTime);
            return 1;
        }
        
        Logger.printSuccess("Syntax analysis completed successfully");
        
        if (syntaxOnly) {
            Logger.printInfo("Syntax-only mode: stopping after syntax analysis");
            Logger.printCompilationResult(true, System.currentTimeMillis() - startTime);
            return 0;
        }

        // Phase 3: Semantic Analysis
        Logger.printPhase("Starting semantic analysis");
        
        SemanticAnalyzer semantic = new SemanticAnalyzer();
        semantic.visit(tree);
        
        if (semantic.getErrorHandler().hasErrors()) {
            semantic.getErrorHandler().printSummary();
            Logger.printError("Compilation failed due to semantic errors");
            Logger.printCompilationResult(false, System.currentTimeMillis() - startTime);
            return 1;
        }
        
        Logger.printSuccess("Semantic analysis completed successfully");
        
        if (semanticOnly) {
            Logger.printInfo("Semantic-only mode: stopping after semantic analysis");
            Logger.printCompilationResult(true, System.currentTimeMillis() - startTime);
            return 0;
        }

        // Phase 4: Code Generation
        Logger.printPhase("Starting code generation");
        
        CodeGenerator codeGen = new CodeGenerator();
        codeGen.visit(tree);
        
        String programName = codeGen.getProgram().getProgramName();
        String outputFile = outputDir + "/" + programName + ".c";
        String generatedCode = codeGen.generateCode();
        
        try {
            Logger.printStep("Writing generated C code to '" + outputFile + "'");
            Files.writeString(Paths.get(outputFile), generatedCode);
            Logger.printSuccess("Code generation completed successfully");
        } catch (IOException e) {
            Logger.printError("Error writing output file: " + e.getMessage());
            return 1;
        }

        if (verbose) {
            Logger.printInfo("Generated C file: " + outputFile);
            Logger.printStats("Generated code size", generatedCode.length() + " characters");
        }

        // Phase 5: Compilation and Execution (if requested)
        if (runAfterCompile) {
            Logger.printPhase("Compiling and running generated code");
            
            Runner runner = new CRunner();
            runner.invoke(outputFile);
            
            // Clean up executable and C file if not keeping
            if (!keepCFile) {
                try {
                    Files.deleteIfExists(Paths.get(outputFile));
                    String executableFile = outputFile.replace(".c", "");
                    Files.deleteIfExists(Paths.get(executableFile));
                    if (verbose) {
                        Logger.printInfo("Cleaned up temporary files");
                    }
                } catch (IOException e) {
                    Logger.printError("Warning: Could not clean up temporary files: " + e.getMessage());
                }
            }
        }

        Logger.printCompilationResult(true, System.currentTimeMillis() - startTime);
        return 0;
    }
}
