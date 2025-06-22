package org.eduscript.datastructures;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class TriggerTest {
    
    private Trigger trigger;
    
    @Before
    public void setUp() {
        trigger = new Trigger();
    }
    
    @Test
    public void testConstructor() {
        assertNotNull(trigger);
        assertNull(trigger.getType());
        assertNull(trigger.getValue());
    }
    
    @Test
    public void testTypeGetterAndSetter() {
        // Test PUSH trigger
        trigger.setType(TriggerType.PUSH);
        assertEquals(TriggerType.PUSH, trigger.getType());
        
        // Test PULL_REQUEST trigger
        trigger.setType(TriggerType.PULL_REQUEST);
        assertEquals(TriggerType.PULL_REQUEST, trigger.getType());
        
        // Test TAG trigger
        trigger.setType(TriggerType.TAG);
        assertEquals(TriggerType.TAG, trigger.getType());
        
        // Test setting to null
        trigger.setType(null);
        assertNull(trigger.getType());
    }
    
    @Test
    public void testValueGetterAndSetter() {
        String value = "main";
        trigger.setValue(value);
        assertEquals(value, trigger.getValue());
        
        // Test with different values
        trigger.setValue("develop");
        assertEquals("develop", trigger.getValue());
        
        trigger.setValue("feature/*");
        assertEquals("feature/*", trigger.getValue());
        
        trigger.setValue("v*");
        assertEquals("v*", trigger.getValue());
        
        // Test with null
        trigger.setValue(null);
        assertNull(trigger.getValue());
        
        // Test with empty string
        trigger.setValue("");
        assertEquals("", trigger.getValue());
    }
    
    @Test
    public void testCompleteTrigger() {
        // Test PUSH trigger on main branch
        trigger.setType(TriggerType.PUSH);
        trigger.setValue("main");
        
        assertEquals(TriggerType.PUSH, trigger.getType());
        assertEquals("main", trigger.getValue());
        
        // Test PULL_REQUEST trigger
        trigger.setType(TriggerType.PULL_REQUEST);
        trigger.setValue("develop");
        
        assertEquals(TriggerType.PULL_REQUEST, trigger.getType());
        assertEquals("develop", trigger.getValue());
        
        // Test TAG trigger with pattern
        trigger.setType(TriggerType.TAG);
        trigger.setValue("v*.*.*");
        
        assertEquals(TriggerType.TAG, trigger.getType());
        assertEquals("v*.*.*", trigger.getValue());
    }
    
    @Test
    public void testTriggerTypeEnum() {
        // Test all enum values exist
        assertNotNull(TriggerType.PUSH);
        assertNotNull(TriggerType.PULL_REQUEST);
        assertNotNull(TriggerType.TAG);
        
        // Test enum valueOf
        assertEquals(TriggerType.PUSH, TriggerType.valueOf("PUSH"));
        assertEquals(TriggerType.PULL_REQUEST, TriggerType.valueOf("PULL_REQUEST"));
        assertEquals(TriggerType.TAG, TriggerType.valueOf("TAG"));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTriggerType() {
        TriggerType.valueOf("INVALID_TYPE");
    }
    
    @Test
    public void testTriggerTypeValues() {
        TriggerType[] values = TriggerType.values();
        assertEquals(3, values.length);
        
        boolean foundPush = false;
        boolean foundPullRequest = false;
        boolean foundTag = false;
        
        for (TriggerType type : values) {
            if (type == TriggerType.PUSH) foundPush = true;
            if (type == TriggerType.PULL_REQUEST) foundPullRequest = true;
            if (type == TriggerType.TAG) foundTag = true;
        }
        
        assertTrue("PUSH type should be present", foundPush);
        assertTrue("PULL_REQUEST type should be present", foundPullRequest);
        assertTrue("TAG type should be present", foundTag);
    }
}
