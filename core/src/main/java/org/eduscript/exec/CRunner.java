package org.eduscript.exec;

import java.io.*;

import org.eduscript.utils.Logger;

public class CRunner extends Runner {

    public CRunner() {
        
    }
    
    @Override
    protected String compile(String file) {
        String outputFile = file.replace(".c", "");
        String sourceFile = file;

        ProcessBuilder pb = new ProcessBuilder("gcc", sourceFile, "-o", outputFile);
        pb.redirectErrorStream(true); // Merge stdout and stderr

        Logger.printPhase("compiling C code with GCC");

        try {
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Logger.printExtern(String.format("[GCC] %s", line));
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Compilation failed with exit code " + exitCode);
                return null;
            }

            System.out.println("Compilation successful: " + outputFile);
            return outputFile;
        } catch (IOException | InterruptedException e) {
            Logger.printError("Exception during compilation: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void run(String executable) {
        if (executable == null) {
            Logger.printError("Executable not found. Skipping execution.");
            return;
        }

        Logger.printPhase("running compiled program");

        ProcessBuilder pb = new ProcessBuilder(executable);
        pb.redirectErrorStream(true);

        try {
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Logger.printExtern(String.format("[%s] %s", executable, line));
                }
            }

            int exitCode = process.waitFor();
            Logger.printInfo("Program exited with code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            Logger.printError("Exception during program execution: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }
}
