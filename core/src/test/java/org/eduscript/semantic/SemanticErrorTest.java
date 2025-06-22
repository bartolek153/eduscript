package org.eduscript.semantic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eduscript.utils.ParserUtils;
import org.junit.Before;
import org.junit.Test;

public class SemanticErrorTest {
    
    private SemanticErrorHandler errorHandler;
    private SemanticAnalyzer analyzer;
    
    @Before
    public void setUp() {
        errorHandler = new SemanticErrorHandler();
        analyzer = new SemanticAnalyzer(errorHandler);
    }
    
    @Test
    public void testSemanticError_DuplicateEnvironmentVariable() {
        String src = """
                pipeline ci-ppl {
                  env {
                    NODE_ENV = "production"
                    NODE_ENV = "development"  # Duplicate env var
                  }
                  
                  every push on main
                  
                  stage build {
                    image "node:18"
                    run "npm install"
                  }
                }
                """;

        var parseTree = ParserUtils.getParseTree(src);
        assertNotNull(parseTree);
        
        analyzer.visit(parseTree);
        
        assertTrue("Should have semantic errors", errorHandler.hasErrors());
        assertEquals(1, errorHandler.getErrorCount());
        assertTrue(errorHandler.getErrors().get(0).getMessage().contains("already defined"));
    }
    
    @Test
    public void testSemanticError_MultipleTriggerBlocks() {
        String src = """
                pipeline ci-ppl {
                  every push on main
                  every tag on v*  # Multiple trigger blocks
                  
                  stage build {
                    image "node:18"
                    run "npm install"
                  }
                }
                """;

        var parseTree = ParserUtils.getParseTree(src);
        assertNotNull(parseTree);
        
        analyzer.visit(parseTree);
        
        assertTrue("Should have semantic errors", errorHandler.hasErrors());
        assertEquals(1, errorHandler.getErrorCount());
        assertTrue(errorHandler.getErrors().get(0).getMessage().contains("only one trigger"));
    }
    
    @Test
    public void testSemanticError_InvalidTriggerType() {
        String src = """
                pipeline ci-ppl {
                  every invalid_trigger on main  # Invalid trigger type
                  
                  stage build {
                    image "node:18"
                    run "npm install"
                  }
                }
                """;

        var parseTree = ParserUtils.getParseTree(src);
        assertNotNull(parseTree);
        
        analyzer.visit(parseTree);
        
        assertTrue("Should have semantic errors", errorHandler.hasErrors());
        assertEquals(1, errorHandler.getErrorCount());
        assertTrue(errorHandler.getErrors().get(0).getMessage().contains("Unknown trigger type"));
    }
    
    @Test
    public void testSemanticError_StageWithoutRunCommands() {
        String src = """
                pipeline ci-ppl {
                  every push on main
                  
                  stage build {
                    image "node:18"
                    # Missing run commands
                  }
                }
                """;

        var parseTree = ParserUtils.getParseTree(src);
        assertNotNull(parseTree);
        
        analyzer.visit(parseTree);
        
        assertTrue("Should have semantic errors", errorHandler.hasErrors());
        assertEquals(1, errorHandler.getErrorCount());
        assertTrue(errorHandler.getErrors().get(0).getMessage().contains("at least one 'run' command"));
    }
    
    @Test
    public void testSemanticError_MultipleNeedsBlocks() {
        String src = """
                pipeline ci-ppl {
                  every push on main
                  
                  stage build {
                    image "node:18"
                    run "npm install"
                  }
                  
                  stage test {
                    image "node:18"
                    run "npm test"
                    needs build
                    needs build  # Duplicate needs block
                  }
                }
                """;

        var parseTree = ParserUtils.getParseTree(src);
        assertNotNull(parseTree);
        
        analyzer.visit(parseTree);
        
        assertTrue("Should have semantic errors", errorHandler.hasErrors());
        assertEquals(1, errorHandler.getErrorCount());
        assertTrue(errorHandler.getErrors().get(0).getMessage().contains("only one 'needs' block"));
    }
    
    @Test
    public void testSemanticError_MultipleConfigBlocks() {
        String src = """
                pipeline ci-ppl {
                  every push on main
                  
                  stage build {
                    image "node:18"
                    run "npm install"
                    config {
                      timeout = "300"
                    }
                    config {
                      retry = "3"  # Duplicate config block
                    }
                  }
                }
                """;

        var parseTree = ParserUtils.getParseTree(src);
        assertNotNull(parseTree);
        
        analyzer.visit(parseTree);
        
        assertTrue("Should have semantic errors", errorHandler.hasErrors());
        assertEquals(1, errorHandler.getErrorCount());
        assertTrue(errorHandler.getErrors().get(0).getMessage().contains("only one 'config' block"));
    }
    
    @Test
    public void testSemanticError_DuplicateConfigurationKey() {
        String src = """
                pipeline ci-ppl {
                  every push on main
                  
                  stage build {
                    image "node:18"
                    run "npm install"
                    config {
                      timeout = "300"
                      timeout = "600"  # Duplicate config key
                    }
                  }
                }
                """;

        var parseTree = ParserUtils.getParseTree(src);
        assertNotNull(parseTree);
        
        analyzer.visit(parseTree);
        
        assertTrue("Should have semantic errors", errorHandler.hasErrors());
        assertEquals(1, errorHandler.getErrorCount());
        assertTrue(errorHandler.getErrors().get(0).getMessage().contains("already defined"));
    }
    
    @Test
    public void testSemanticError_ValidPipeline_NoErrors() {
        String src = """
                pipeline ci-ppl {
                  env {
                    NODE_ENV = "production"
                    API_URL = "https://api.example.com"
                  }
                  
                  every push on main
                  
                  stage build {
                    image "node:18"
                    run "npm install"
                    run "npm run build"
                    config {
                      timeout = "300"
                      retry = "3"
                    }
                  }
                  
                  stage test {
                    image "node:18"
                    run "npm test"
                    needs build
                  }
                  
                  stage deploy {
                    image "alpine:latest"
                    run "echo 'deploying'"
                    needs build, test
                  }
                }
                """;

        var parseTree = ParserUtils.getParseTree(src);
        assertNotNull(parseTree);
        
        analyzer.visit(parseTree);
        
        assertFalse("Should not have semantic errors", errorHandler.hasErrors());
        assertEquals(0, errorHandler.getErrorCount());
    }
    
    @Test
    public void testSemanticError_ExportPipelineObject() {
        String src = """
                pipeline test-pipeline {
                  env {
                    NODE_ENV = "production"
                  }
                  
                  every push on main
                  
                  stage build {
                    image "node:18"
                    run "npm install"
                    run "npm run build"
                  }
                }
                """;

        var parseTree = ParserUtils.getParseTree(src);
        assertNotNull(parseTree);
        
        analyzer.visit(parseTree);
        
        assertFalse("Should not have semantic errors", errorHandler.hasErrors());
        
        var pipeline = analyzer.exportPipelineObj();
        assertNotNull(pipeline);
        assertEquals("test-pipeline", pipeline.getName());
        assertNotNull(pipeline.getEnvs());
        assertEquals("production", pipeline.getEnvs().get("NODE_ENV"));
        assertNotNull(pipeline.getTrg());
        assertNotNull(pipeline.getPlan());
        assertEquals(1, pipeline.getPlan().size());
        assertEquals("build", pipeline.getPlan().get(0).getName());
    }
    
    @Test
    public void testSemanticError_MultipleSemanticErrors() {
        String src = """
                pipeline ci-ppl {
                  env {
                    NODE_ENV = "production"
                    NODE_ENV = "development"  # Error 1: Duplicate env var
                  }
                  
                  every push on main
                  every tag on v*  # Error 2: Multiple triggers
                  
                  stage build {
                    image "node:18"
                    # Error 3: Missing run commands
                  }
                  
                  stage test {
                    image "node:18"
                    run "npm test"
                    config {
                      timeout = "300"
                      timeout = "600"  # Error 4: Duplicate config key
                    }
                  }
                }
                """;

        var parseTree = ParserUtils.getParseTree(src);
        assertNotNull(parseTree);
        
        analyzer.visit(parseTree);
        
        assertTrue("Should have semantic errors", errorHandler.hasErrors());
        assertEquals(4, errorHandler.getErrorCount());
    }
}
