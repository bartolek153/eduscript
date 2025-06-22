package org.eduscript.datastructures;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

public class StageConfigsTest {
    
    private StageConfigs stageConfigs;
    
    @Before
    public void setUp() {
        stageConfigs = new StageConfigs();
    }
    
    @Test
    public void testConstructor() {
        assertNotNull(stageConfigs);
        assertNotNull(stageConfigs.getCustomArgs());
        assertTrue(stageConfigs.getCustomArgs().isEmpty());
    }
    
    @Test
    public void testAddCustomArg() {
        String key = "timeout";
        String value = "300";
        
        stageConfigs.addCustomArg(key, value);
        
        assertEquals(1, stageConfigs.getCustomArgs().size());
        assertEquals(value, stageConfigs.getCustomArgs().get(key));
        assertTrue(stageConfigs.customArgAlreadyDefined(key));
    }
    
    @Test
    public void testAddMultipleCustomArgs() {
        stageConfigs.addCustomArg("timeout", "300");
        stageConfigs.addCustomArg("retry", "3");
        stageConfigs.addCustomArg("memory", "512m");
        
        assertEquals(3, stageConfigs.getCustomArgs().size());
        assertEquals("300", stageConfigs.getCustomArgs().get("timeout"));
        assertEquals("3", stageConfigs.getCustomArgs().get("retry"));
        assertEquals("512m", stageConfigs.getCustomArgs().get("memory"));
        
        assertTrue(stageConfigs.customArgAlreadyDefined("timeout"));
        assertTrue(stageConfigs.customArgAlreadyDefined("retry"));
        assertTrue(stageConfigs.customArgAlreadyDefined("memory"));
    }
    
    @Test
    public void testCustomArgAlreadyDefined() {
        String key = "environment";
        
        // Initially not defined
        assertFalse(stageConfigs.customArgAlreadyDefined(key));
        
        // Add the key
        stageConfigs.addCustomArg(key, "production");
        
        // Now it should be defined
        assertTrue(stageConfigs.customArgAlreadyDefined(key));
    }
    
    @Test
    public void testCustomArgAlreadyDefinedWithNullKey() {
        assertFalse(stageConfigs.customArgAlreadyDefined(null));
    }
    
    @Test
    public void testCustomArgAlreadyDefinedWithEmptyKey() {
        assertFalse(stageConfigs.customArgAlreadyDefined(""));
    }
    
    @Test
    public void testSetCustomArgs() {
        Map<String, String> customArgs = new HashMap<>();
        customArgs.put("cpu", "2");
        customArgs.put("memory", "1g");
        customArgs.put("disk", "10g");
        
        stageConfigs.setCustomArgs(customArgs);
        
        assertEquals(customArgs, stageConfigs.getCustomArgs());
        assertEquals(3, stageConfigs.getCustomArgs().size());
        assertTrue(stageConfigs.customArgAlreadyDefined("cpu"));
        assertTrue(stageConfigs.customArgAlreadyDefined("memory"));
        assertTrue(stageConfigs.customArgAlreadyDefined("disk"));
    }
    
    @Test
    public void testOverwriteCustomArg() {
        String key = "timeout";
        String originalValue = "300";
        String newValue = "600";
        
        stageConfigs.addCustomArg(key, originalValue);
        assertEquals(originalValue, stageConfigs.getCustomArgs().get(key));
        
        stageConfigs.addCustomArg(key, newValue);
        assertEquals(newValue, stageConfigs.getCustomArgs().get(key));
        assertEquals(1, stageConfigs.getCustomArgs().size());
    }
    
    @Test
    public void testAddCustomArgWithNullKey() {
        stageConfigs.addCustomArg(null, "value");
        // Should handle gracefully - check if it doesn't crash
        // The behavior depends on HashMap implementation
        assertNotNull(stageConfigs.getCustomArgs());
    }
    
    @Test
    public void testAddCustomArgWithNullValue() {
        String key = "test-key";
        stageConfigs.addCustomArg(key, null);
        
        assertTrue(stageConfigs.customArgAlreadyDefined(key));
        assertNull(stageConfigs.getCustomArgs().get(key));
    }
    
    @Test
    public void testAddCustomArgWithEmptyValue() {
        String key = "empty-value";
        String emptyValue = "";
        
        stageConfigs.addCustomArg(key, emptyValue);
        
        assertTrue(stageConfigs.customArgAlreadyDefined(key));
        assertEquals(emptyValue, stageConfigs.getCustomArgs().get(key));
    }
    
    @Test
    public void testCaseSensitiveKeys() {
        stageConfigs.addCustomArg("Timeout", "300");
        stageConfigs.addCustomArg("timeout", "600");
        
        assertEquals(2, stageConfigs.getCustomArgs().size());
        assertTrue(stageConfigs.customArgAlreadyDefined("Timeout"));
        assertTrue(stageConfigs.customArgAlreadyDefined("timeout"));
        assertFalse(stageConfigs.customArgAlreadyDefined("TIMEOUT"));
        
        assertEquals("300", stageConfigs.getCustomArgs().get("Timeout"));
        assertEquals("600", stageConfigs.getCustomArgs().get("timeout"));
    }
}
