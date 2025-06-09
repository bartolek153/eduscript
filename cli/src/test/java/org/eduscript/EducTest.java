package org.eduscript;

import static org.junit.Assert.*;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.*;

import picocli.CommandLine;

public class EducTest {
    
    private CommandLine getCommandLine() {
        CommandLine cmd = new CommandLine(new Educ());
        cmd.setOut(new PrintWriter(new StringWriter()));
        
        return cmd;
    }
    
    @Test
    public void testBasicCommand() {
    }
}
