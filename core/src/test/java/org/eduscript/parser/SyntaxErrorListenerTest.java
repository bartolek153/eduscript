package org.eduscript.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class SyntaxErrorListenerTest {
    
    private SyntaxErrorListener errorListener;
    
    @Before
    public void setUp() {
        errorListener = new SyntaxErrorListener();
    }
    
    @Test
    public void testInitialState() {
        assertFalse("Should not have errors initially", errorListener.hasError());
        assertEquals("", errorListener.getErrorMessages());
    }
    
    @Test
    public void testMultipleSyntaxErrors() {
        // Report first error
        errorListener.syntaxError(null, null, 1, 5, "First error", null);
        
        // Report second error
        errorListener.syntaxError(null, null, 3, 15, "Second error", null);
        
        assertTrue("Should have errors", errorListener.hasError());
        String errorMessages = errorListener.getErrorMessages();
        
        assertTrue("Should contain first error", errorMessages.contains("First error"));
        assertTrue("Should contain second error", errorMessages.contains("Second error"));
        assertTrue("Should contain first line", errorMessages.contains("1"));
        assertTrue("Should contain second line", errorMessages.contains("3"));
    }
    
    @Test
    public void testSyntaxErrorWithNullMessage() {
        errorListener.syntaxError(null, null, 1, 1, null, null);
        
        assertTrue("Should have errors even with null message", errorListener.hasError());
        String errorMessages = errorListener.getErrorMessages();
        assertFalse("Error messages should not be empty", errorMessages.isEmpty());
    }
    
    @Test
    public void testSyntaxErrorWithEmptyMessage() {
        errorListener.syntaxError(null, null, 1, 1, "", null);
        
        assertTrue("Should have errors even with empty message", errorListener.hasError());
        String errorMessages = errorListener.getErrorMessages();
        assertFalse("Error messages should not be empty", errorMessages.isEmpty());
    }
    
    @Test
    public void testSyntaxErrorWithZeroLineColumn() {
        errorListener.syntaxError(null, null, 0, 0, "Error at start", null);
        
        assertTrue("Should have errors", errorListener.hasError());
        String errorMessages = errorListener.getErrorMessages();
        assertTrue("Should contain error message", errorMessages.contains("Error at start"));
        assertTrue("Should contain line 0", errorMessages.contains("0"));
    }
    
    @Test
    public void testSyntaxErrorWithNegativeLineColumn() {
        errorListener.syntaxError(null, null, -1, -1, "Negative position error", null);
        
        assertTrue("Should have errors", errorListener.hasError());
        String errorMessages = errorListener.getErrorMessages();
        assertTrue("Should contain error message", errorMessages.contains("Negative position error"));
    }
    
    @Test
    public void testMultipleErrorsFormatting() {
        errorListener.syntaxError(null, null, 1, 1, "Error one", null);
        errorListener.syntaxError(null, null, 2, 2, "Error two", null);
        errorListener.syntaxError(null, null, 3, 3, "Error three", null);
        
        String errorMessages = errorListener.getErrorMessages();
        
        // Should contain all three errors
        assertTrue("Should contain first error", errorMessages.contains("Error one"));
        assertTrue("Should contain second error", errorMessages.contains("Error two"));
        assertTrue("Should contain third error", errorMessages.contains("Error three"));
        
        // Should contain line numbers for all errors
        assertTrue("Should contain line 1", errorMessages.contains("1"));
        assertTrue("Should contain line 2", errorMessages.contains("2"));
        assertTrue("Should contain line 3", errorMessages.contains("3"));
    }
    
    @Test
    public void testErrorAccumulation() {
        // Initially no errors
        assertFalse(errorListener.hasError());
        assertEquals("", errorListener.getErrorMessages());
        
        // Add first error
        errorListener.syntaxError(null, null, 1, 1, "First", null);
        assertTrue(errorListener.hasError());
        String firstMessage = errorListener.getErrorMessages();
        assertFalse(firstMessage.isEmpty());
        
        // Add second error
        errorListener.syntaxError(null, null, 2, 2, "Second", null);
        assertTrue(errorListener.hasError());
        String secondMessage = errorListener.getErrorMessages();
        
        // Second message should be longer than first (contains both errors)
        assertTrue("Second message should contain more content", 
                   secondMessage.length() > firstMessage.length());
        assertTrue("Should still contain first error", secondMessage.contains("First"));
        assertTrue("Should contain second error", secondMessage.contains("Second"));
    }
    
    @Test
    public void testLongErrorMessage() {
        String longMessage = "This is a very long error message that contains multiple words and should be handled properly by the error listener without any issues or truncation problems";
        
        errorListener.syntaxError(null, null, 5, 10, longMessage, null);
        
        assertTrue("Should have errors", errorListener.hasError());
        String errorMessages = errorListener.getErrorMessages();
        assertTrue("Should contain the full long message", errorMessages.contains(longMessage));
    }
    
    @Test
    public void testSpecialCharactersInErrorMessage() {
        String specialMessage = "Error with special chars: !@#$%^&*()_+-={}[]|\\:;\"'<>?,./";
        
        errorListener.syntaxError(null, null, 1, 1, specialMessage, null);
        
        assertTrue("Should have errors", errorListener.hasError());
        String errorMessages = errorListener.getErrorMessages();
        assertTrue("Should contain special characters", errorMessages.contains(specialMessage));
    }
}
