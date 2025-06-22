package org.eduscript.semantic;

import static org.junit.Assert.*;

import org.eduscript.datastructures.Pipeline;
import org.eduscript.datastructures.Stage;
import org.eduscript.datastructures.TriggerType;
import org.eduscript.utils.ParserUtils;
import org.junit.Test;
import org.junit.Before;

public class SemanticAnalyzerTest {
    
    private SemanticErrorHandler errorHandler;
    private SemanticAnalyzer analyzer;
    
    @Before
    public void setUp() {
        errorHandler = new SemanticErrorHandler();
        analyzer = new SemanticAnalyzer(errorHandler);
    }
    
    @Test
    public void testAnalyze_SimplePipeline() {
        String src = """
                pipeline simple-ci {
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
        
        assertFalse("Should not have errors", errorHandler.hasErrors());
        
        Pipeline pipeline = analyzer.exportPipelineObj();
        assertNotNull(pipeline);
        assertEquals("simple-ci", pipeline.getName());
        assertNotNull(pipeline.getTrg());
        assertEquals(TriggerType.PUSH, pipeline.getTrg().getType());
        assertEquals("main", pipeline.getTrg().getValue());
        assertEquals(1, pipeline.getPlan().size());
        
        Stage buildStage = pipeline.getPlan().get(0);
        assertEquals("build", buildStage.getName());
        assertEquals("node:18", buildStage.getImage());
        assertEquals(2, buildStage.getRunCommands().size());
        assertEquals("npm install", buildStage.getRunCommands().get(0));
        assertEquals("npm run build", buildStage.getRunCommands().get(1));
    }
    
    @Test
    public void testAnalyze_PipelineWithEnvironments() {
        String src = """
                pipeline env-test {
                  env {
                    NODE_ENV = "production"
                    API_URL = "https://api.example.com"
                    DEBUG = "false"
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
        
        assertFalse("Should not have errors", errorHandler.hasErrors());
        
        Pipeline pipeline = analyzer.exportPipelineObj();
        assertNotNull(pipeline.getEnvs());
        assertEquals(3, pipeline.getEnvs().size());
        assertEquals("production", pipeline.getEnvs().get("NODE_ENV"));
        assertEquals("https://api.example.com", pipeline.getEnvs().get("API_URL"));
        assertEquals("false", pipeline.getEnvs().get("DEBUG"));
    }
    
    @Test
    public void testAnalyze_PipelineWithDifferentTriggerTypes() {
        // Test PUSH trigger
        String pushSrc = """
                pipeline push-test {
                  every push on develop
                  
                  stage build {
                    image "alpine:latest"
                    run "echo 'push trigger'"
                  }
                }
                """;

        var pushTree = ParserUtils.getParseTree(pushSrc);
        analyzer.visit(pushTree);
        
        Pipeline pushPipeline = analyzer.exportPipelineObj();
        assertEquals(TriggerType.PUSH, pushPipeline.getTrg().getType());
        assertEquals("develop", pushPipeline.getTrg().getValue());
        
        // Reset for next test
        setUp();
        
        // Test PULL_REQUEST trigger
        String prSrc = """
                pipeline pr-test {
                  every pull_request on main
                  
                  stage test {
                    image "alpine:latest"
                    run "echo 'pr trigger'"
                  }
                }
                """;

        var prTree = ParserUtils.getParseTree(prSrc);
        analyzer.visit(prTree);
        
        Pipeline prPipeline = analyzer.exportPipelineObj();
        assertEquals(TriggerType.PULL_REQUEST, prPipeline.getTrg().getType());
        assertEquals("main", prPipeline.getTrg().getValue());
        
        // Reset for next test
        setUp();
        
        // Test TAG trigger
        String tagSrc = """
                pipeline tag-test {
                  every tag on "v*"
                  
                  stage deploy {
                    image "alpine:latest"
                    run "echo 'tag trigger'"
                  }
                }
                """;

        var tagTree = ParserUtils.getParseTree(tagSrc);
        analyzer.visit(tagTree);
        
        Pipeline tagPipeline = analyzer.exportPipelineObj();
        assertEquals(TriggerType.TAG, tagPipeline.getTrg().getType());
        assertEquals("v*", tagPipeline.getTrg().getValue());
    }
    
    @Test
    public void testAnalyze_StageWithDependencies() {
        String src = """
                pipeline deps-test {
                  every push on main
                  
                  stage build {
                    image "node:18"
                    run "npm install"
                    run "npm run build"
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
        
        assertFalse("Should not have errors", errorHandler.hasErrors());
        
        Pipeline pipeline = analyzer.exportPipelineObj();
        assertEquals(3, pipeline.getPlan().size());
        
        Stage buildStage = pipeline.getPlan().get(0);
        assertEquals("build", buildStage.getName());
        assertFalse(buildStage.hasDeps());
        
        Stage testStage = pipeline.getPlan().get(1);
        assertEquals("test", testStage.getName());
        assertTrue(testStage.hasDeps());
        assertEquals(1, testStage.getDeps().size());
        assertEquals("build", testStage.getDeps().get(0));
        
        Stage deployStage = pipeline.getPlan().get(2);
        assertEquals("deploy", deployStage.getName());
        assertTrue(deployStage.hasDeps());
        assertEquals(2, deployStage.getDeps().size());
        assertTrue(deployStage.getDeps().contains("build"));
        assertTrue(deployStage.getDeps().contains("test"));
    }
    
    @Test
    public void testAnalyze_StageWithConfig() {
        String src = """
                pipeline config-test {
                  every push on main
                  
                  stage build {
                    image "node:18"
                    run "npm install"
                    config {
                      timeout = "300"
                      retry = "3"
                      memory = "512m"
                    }
                  }
                }
                """;

        var parseTree = ParserUtils.getParseTree(src);
        assertNotNull(parseTree);
        
        analyzer.visit(parseTree);
        
        assertFalse("Should not have errors", errorHandler.hasErrors());
        
        Pipeline pipeline = analyzer.exportPipelineObj();
        Stage buildStage = pipeline.getPlan().get(0);
        
        assertNotNull(buildStage.getConfig());
        assertEquals(3, buildStage.getConfig().getCustomArgs().size());
        assertEquals("300", buildStage.getConfig().getCustomArgs().get("timeout"));
        assertEquals("3", buildStage.getConfig().getCustomArgs().get("retry"));
        assertEquals("512m", buildStage.getConfig().getCustomArgs().get("memory"));
    }
    
    @Test
    public void testAnalyze_ComplexPipeline() {
        String src = """
                pipeline complex-ci {
                  env {
                    NODE_ENV = "production"
                    API_URL = "https://api.example.com"
                  }
                  
                  every push on main
                  
                  stage build {
                    image "node:18"
                    run "npm ci"
                    run "npm run build"
                    config {
                      timeout = "600"
                    }
                  }
                  
                  stage test {
                    image "node:18"
                    run "npm run test:unit"
                    run "npm run test:integration"
                    needs build
                    config {
                      parallel = "true"
                    }
                  }
                  
                  stage lint {
                    image "node:18"
                    run "npm run lint"
                    needs build
                  }
                  
                  stage deploy {
                    image "alpine:latest"
                    run "echo 'Deploying to production'"
                    run "kubectl apply -f deployment.yaml"
                    needs build, test, lint
                    config {
                      environment = "production"
                      timeout = "1200"
                    }
                  }
                }
                """;

        var parseTree = ParserUtils.getParseTree(src);
        assertNotNull(parseTree);
        
        analyzer.visit(parseTree);
        
        assertFalse("Should not have errors", errorHandler.hasErrors());
        
        Pipeline pipeline = analyzer.exportPipelineObj();
        
        // Verify pipeline properties
        assertEquals("complex-ci", pipeline.getName());
        assertEquals(2, pipeline.getEnvs().size());
        assertEquals(TriggerType.PUSH, pipeline.getTrg().getType());
        assertEquals("main", pipeline.getTrg().getValue());
        assertEquals(4, pipeline.getPlan().size());
        
        // Verify stages
        Stage buildStage = pipeline.getPlan().get(0);
        assertEquals("build", buildStage.getName());
        assertEquals(2, buildStage.getRunCommands().size());
        assertNotNull(buildStage.getConfig());
        
        Stage testStage = pipeline.getPlan().get(1);
        assertEquals("test", testStage.getName());
        assertTrue(testStage.hasDeps());
        assertEquals(1, testStage.getDeps().size());
        
        Stage lintStage = pipeline.getPlan().get(2);
        assertEquals("lint", lintStage.getName());
        assertTrue(lintStage.hasDeps());
        
        Stage deployStage = pipeline.getPlan().get(3);
        assertEquals("deploy", deployStage.getName());
        assertTrue(deployStage.hasDeps());
        assertEquals(3, deployStage.getDeps().size());
        assertNotNull(deployStage.getConfig());
        assertEquals(2, deployStage.getConfig().getCustomArgs().size());
    }
    
    @Test
    public void testAnalyze_WithMultilineRunCommand() {
        String src = """
                pipeline multiline-test {
                  every push on main
                  
                  stage build {
                    image "alpine:latest"
                    run \"\"\"
                    echo "Starting build"
                    apk add --no-cache nodejs npm
                    npm install
                    npm run build
                    echo "Build completed"
                    \"\"\"
                  }
                }
                """;

        var parseTree = ParserUtils.getParseTree(src);
        assertNotNull(parseTree);
        
        analyzer.visit(parseTree);
        
        assertFalse("Should not have errors", errorHandler.hasErrors());
        
        Pipeline pipeline = analyzer.exportPipelineObj();
        Stage buildStage = pipeline.getPlan().get(0);
        
        assertEquals(1, buildStage.getRunCommands().size());
        String multilineCommand = buildStage.getRunCommands().get(0);
        assertTrue(multilineCommand.contains("echo \"Starting build\""));
        assertTrue(multilineCommand.contains("npm install"));
    }
    
    @Test
    public void testGetErrorHandler() {
        assertSame(errorHandler, analyzer.getErrorHandler());
    }
}
