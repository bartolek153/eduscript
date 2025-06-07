package org.eduscript;

import java.util.concurrent.Callable;

import org.eduscript.utils.Logger;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "educ", mixinStandardHelpOptions = true, version = "EduScript compiler 1.0",
    description = "Compiles .edu code and execute.")
public class Educ implements Callable<Integer> {

    @Option(names = {"-t", "--test"}, description = "testing, ...")
    private String algorithm = "test...";
    
    @Override
    public Integer call() throws Exception {
        Logger.printHeader("EduScript Compiler" + algorithm);
        return 0;
    }

    // this example implements Callable, so 
    // parsing, error handling and handling user requests 
    // for usage help or version help can be done with one line of code.
    public static void main(String[] args) {
        int exitCode = new CommandLine(new Educ()).execute(args);
        System.exit(exitCode);
    }
}
