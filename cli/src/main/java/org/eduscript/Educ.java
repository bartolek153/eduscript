package org.eduscript;

import java.util.concurrent.Callable;

import org.eduscript.commands.Check;
import org.eduscript.commands.Compile;
import org.eduscript.commands.Run;
import org.eduscript.utils.Logger;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "educ", 
         mixinStandardHelpOptions = true, 
         version = "EduScript Compiler 1.0.0",
         description = "EduScript compiler and development tools",
         subcommands = {
             Compile.class,
             Run.class,
             Check.class,
             CommandLine.HelpCommand.class
         })
public class Educ implements Callable<Integer> {

    @Option(names = {"-v", "--version"}, 
            versionHelp = true, 
            description = "Display version information")
    private boolean versionRequested;

    @Override
    public Integer call() throws Exception {
        // When called without subcommands, show help and basic info
        Logger.printHeader("EduScript Compiler 1.0.0");
        System.out.println();
        System.out.println("A compiler for the EduScript educational programming language.");
        System.out.println("EduScript compiles to C code and can execute programs directly.");
        System.out.println();
        System.out.println("Usage: educ <command> [options] <file>");
        System.out.println();
        System.out.println("Commands:");
        System.out.println("  compile    Compile EduScript files to C");
        System.out.println("  run        Compile and run EduScript files directly");
        System.out.println("  check      Check syntax and semantics without compilation");
        System.out.println("  help       Show help for commands");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  educ run program.edu              # Run a program directly");
        System.out.println("  educ compile program.edu          # Compile to C");
        System.out.println("  educ compile -r program.edu       # Compile and run");
        System.out.println("  educ check program.edu            # Check for errors");
        System.out.println();
        System.out.println("Use 'educ <command> --help' for more information about a command.");
        
        return 0;
    }

    public static void main(String[] args) {
        CommandLine cmd = new CommandLine(new Educ());
        
        // Configure command line parsing
        cmd.setUsageHelpAutoWidth(true);
        cmd.setAbbreviatedOptionsAllowed(true);
        cmd.setAbbreviatedSubcommandsAllowed(true);
        
        // Execute command
        int exitCode = cmd.execute(args);
        System.exit(exitCode);
    }
}
