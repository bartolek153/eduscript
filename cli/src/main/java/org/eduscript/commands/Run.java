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

@Command(name = "run", 
         description = "Compile and run EduScript source files directly",
         mixinStandardHelpOptions = true)
public class Run implements Callable<Integer> {

    @Parameters(index = "0", 
                description = "Input EduScript source file (.edu)")
    private String inputFile;

    @Option(names = {"-v", "--verbose"}, 
            description = "Enable verbose output")
    private boolean verbose = false;

    @Option(names = {"-k", "--keep-files"}, 
            description = "Keep generated C file and executable after running")
    private boolean keepFiles = false;

    @Option(names = {"--temp-dir"}, 
            description = "Temporary directory for generated files (default: temp/)")
    private String tempDir = "temp/";

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

        // Create temp directory if it doesn't exist
        Path tempPath = Paths.get(tempDir);
        try {
            Files.createDirectories(tempPath);
        } catch (IOException e) {
            System.err.println("Error: Could not create temp directory: " + e.getMessage());
            return 1;
        }

        // Read source file
        String source;
        try {
            if (verbose) {
                Logger.printFileInfo("Reading source file", inputFile);
            }
            source = Files.readString(inputPath);
            if (verbose) {
                Logger.printSuccess("Source file loaded successfully");
                Logger.printStats("File size", Files.size(inputPath) + " bytes");
            }
        } catch (IOException e) {
            Logger.printError("Failed to read source file: " + e.getMessage());
            return 1;
        }

        // Quick compilation phases
        if (verbose) {
            Logger.printSeparator();
            Logger.printPhase("Starting compilation");
        }
        
        // Lexical and Syntax Analysis
        CharStream input = CharStreams.fromString(source);
        EduScriptLexer lexer = new EduScriptLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        
        EduScriptParser parser = new EduScriptParser(tokens);
        SyntaxErrorListener errorListener = new SyntaxErrorListener();
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);
        
        ParseTree tree = parser.program();
        
        if (errorListener.hasError()) {
            Logger.printError("Compilation failed: syntax errors found");
            return 1;
        }
        
        // Semantic Analysis
        SemanticAnalyzer semantic = new SemanticAnalyzer();
        semantic.visit(tree);
        
        if (semantic.getErrorHandler().hasErrors()) {
            semantic.getErrorHandler().printSummary();
            Logger.printError("Compilation failed: semantic errors found");
            return 1;
        }
        
        // Code Generation
        CodeGenerator codeGen = new CodeGenerator();
        codeGen.visit(tree);
        
        String programName = codeGen.getProgram().getProgramName();
        String outputFile = tempDir + "/" + programName + ".c";
        String generatedCode = codeGen.generateCode();
        
        try {
            Files.writeString(Paths.get(outputFile), generatedCode);
            if (verbose) {
                Logger.printSuccess("Code generation completed");
                Logger.printInfo("Generated C file: " + outputFile);
            }
        } catch (IOException e) {
            Logger.printError("Error writing temporary C file: " + e.getMessage());
            return 1;
        }

        // Compile and Run
        if (verbose) {
            Logger.printPhase("Running program");
        } else {
            Logger.printInfo("Running " + inputFile + "...");
            Logger.printSeparator();
        }
        
        Runner runner = new CRunner();
        runner.invoke(outputFile);
        
        // Clean up temporary files unless keeping them
        if (!keepFiles) {
            try {
                Files.deleteIfExists(Paths.get(outputFile));
                String executableFile = outputFile.replace(".c", "");
                Files.deleteIfExists(Paths.get(executableFile));
                
                // Try to remove temp directory if empty
                try {
                    Files.deleteIfExists(tempPath);
                } catch (IOException e) {
                    // Ignore - directory might not be empty
                }
                
                if (verbose) {
                    Logger.printInfo("Cleaned up temporary files");
                }
            } catch (IOException e) {
                if (verbose) {
                    Logger.printError("Warning: Could not clean up temporary files: " + e.getMessage());
                }
            }
        } else if (verbose) {
            Logger.printInfo("Temporary files kept in: " + tempDir);
        }

        if (verbose) {
            Logger.printCompilationResult(true, System.currentTimeMillis() - startTime);
        }
        return 0;
    }
}
