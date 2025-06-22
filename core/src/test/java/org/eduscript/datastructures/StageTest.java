package org.eduscript.datastructures;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StageTest {
    
    private Stage stage;
    private String testName;
    
    @Before
    public void setUp() {
        testName = "test-stage";
        stage = new Stage(testName);
    }
    
    @Test
    public void testConstructor() {
        assertNotNull(stage);
        assertEquals(testName, stage.getName());
        assertNotNull(stage.getRunCommands());
        assertTrue(stage.getRunCommands().isEmpty());
        assertNotNull(stage.getDeps());
        assertTrue(stage.getDeps().isEmpty());
        assertNull(stage.getImage());
        assertNull(stage.getConfig());
    }
    
    @Test
    public void testNameGetterAndSetter() {
        String newName = "updated-stage";
        stage.setName(newName);
        assertEquals(newName, stage.getName());
    }
    
    @Test
    public void testImageGetterAndSetter() {
        String image = "node:18";
        stage.setImage(image);
        assertEquals(image, stage.getImage());
    }
    
    @Test
    public void testRunCommands() {
        // Test initial state
        assertFalse(stage.hasAtLeastOneRunCommand());
        
        // Test adding single command
        String command1 = "npm install";
        stage.addRunCommand(command1);
        assertTrue(stage.hasAtLeastOneRunCommand());
        assertEquals(1, stage.getRunCommands().size());
        assertEquals(command1, stage.getRunCommands().get(0));
        
        // Test adding multiple commands
        String command2 = "npm run build";
        stage.addRunCommand(command2);
        assertEquals(2, stage.getRunCommands().size());
        assertEquals(command2, stage.getRunCommands().get(1));
        
        // Test setting run commands list
        List<String> newCommands = Arrays.asList("echo 'hello'", "echo 'world'");
        stage.setRunCommands(newCommands);
        assertEquals(newCommands, stage.getRunCommands());
        assertTrue(stage.hasAtLeastOneRunCommand());
    }
    
    @Test
    public void testDependencies() {
        // Test initial state
        assertFalse(stage.hasDeps());
        
        // Test setting dependencies
        List<String> deps = Arrays.asList("build", "test");
        stage.setDeps(deps);
        assertTrue(stage.hasDeps());
        assertEquals(deps, stage.getDeps());
        assertEquals(2, stage.getDeps().size());
        
        // Test empty dependencies
        stage.setDeps(Arrays.asList());
        assertFalse(stage.hasDeps());
    }
    
    @Test
    public void testConfig() {
        assertNull(stage.getConfig());
        
        StageConfigs config = new StageConfigs();
        config.addCustomArg("timeout", "300");
        stage.setConfig(config);
        
        assertNotNull(stage.getConfig());
        assertEquals(config, stage.getConfig());
    }
    
    @Test
    public void testCompleteStage() {
        // Create a complete stage
        stage.setImage("alpine:latest");
        stage.addRunCommand("apk add --no-cache curl");
        stage.addRunCommand("curl -f http://example.com");
        stage.setDeps(Arrays.asList("build"));
        
        StageConfigs config = new StageConfigs();
        config.addCustomArg("retry", "3");
        stage.setConfig(config);
        
        // Verify all properties
        assertEquals("alpine:latest", stage.getImage());
        assertTrue(stage.hasAtLeastOneRunCommand());
        assertEquals(2, stage.getRunCommands().size());
        assertTrue(stage.hasDeps());
        assertEquals(1, stage.getDeps().size());
        assertEquals("build", stage.getDeps().get(0));
        assertNotNull(stage.getConfig());
    }
    
    @Test
    public void testEmptyRunCommands() {
        stage.setRunCommands(Arrays.asList());
        assertFalse(stage.hasAtLeastOneRunCommand());
        assertTrue(stage.getRunCommands().isEmpty());
    }
    
    @Test
    public void testNullName() {
        Stage nullNameStage = new Stage(null);
        assertNull(nullNameStage.getName());
    }
}
