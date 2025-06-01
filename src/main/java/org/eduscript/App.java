package org.eduscript;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.eduscript.codegen.CodeGenerator;
import org.eduscript.parser.SyntaxErrorListener;
import org.eduscript.semantic.SemanticAnalyzer;

import main.antlr4.EduScriptLexer;
import main.antlr4.EduScriptParser;

public class App
{
    public static void main( String[] args ) {
        String source;
        String outputFile = "output/";
        
        if (args.length > 0 && !args[0].startsWith("-D")) {
            // Read from file
            try {
                source = new String(Files.readAllBytes(Paths.get(args[0])));
                if (args.length > 1) {
                    outputFile = args[1];
                }
            } catch (IOException e) {
                System.err.println("Error reading file: " + e.getMessage());
                return;
            }
        } else {
            // Use default test program
            source = """
                programa TestSemanticErrors;

                    var x: inteiro;
                    var x: real;  // Duplicate variable declaration

                    inicio
                        y = 10;   // Undeclared variable
                        x = "hello";  // Type mismatch
                        x = 5 + "world";  // Type mismatch in operation
                    fimprograma
                """;
        }

        CharStream input = CharStreams.fromString(source);
        EduScriptLexer lexer = new EduScriptLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        EduScriptParser parser = new EduScriptParser(tokens);
        SyntaxErrorListener errorListener = new SyntaxErrorListener();
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);

        ParseTree tree = parser.program();

        // Check for syntax errors and stop execution if any are found
        if (errorListener.hasError()) {
            System.err.println("\nCompilation failed due to syntax errors.");
            System.exit(1);
            return;
        }
        
        System.out.println("✓ Syntax analysis completed successfully.\n");

        // First pass: Semantic Analysis
        System.out.println("=== Semantic Analysis ===");
        SemanticAnalyzer semantic = new SemanticAnalyzer();
        semantic.visit(tree);
        
        // Check for semantic errors and stop execution if any are found
        if (semantic.getErrorHandler().hasErrors()) {
            semantic.getErrorHandler().printSummary();
            System.err.println("\nCompilation failed due to semantic errors.");
            System.exit(1);
            return;
        }
        
        semantic.getErrorHandler().printSummary();
        
        // Second pass: Code Generation
        System.out.println("\n=== Code Generation ===");
        CodeGenerator codeGen = new CodeGenerator();
        codeGen.visit(tree);
        outputFile += codeGen.getProgram().getProgramName() + ".c";
        
        String generatedCode = codeGen.generateCode();
        System.out.println("Generated C code:");
        System.out.println(generatedCode);
        
        // Write to file
        try {
            Files.write(Paths.get(outputFile), generatedCode.getBytes());
            System.out.println("\nC code written to: " + outputFile);
        } catch (IOException e) {
            System.err.println("Error writing output file: " + e.getMessage());
        }
    }
}
