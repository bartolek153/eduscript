package org.eduscript.parser;

import static org.junit.Assert.*;

import org.eduscript.utils.ParserUtils;
import org.junit.Test;

import main.antlr4.EduScriptParser;

public class ParserErrorTest {
    
    @Test
    public void testSyntaxError_MissingClosingBrace() {
        String src = """
                pipeline ci-ppl {
                  every push on main
                  
                  stage build {
                    image "node:18"
                    run "npm install"
                  # Missing closing brace for pipeline
                """;

        EduScriptParser parser = ParserUtils.getParser(src);
        parser.pipeline();
        
        assertTrue("Should have syntax errors", parser.getNumberOfSyntaxErrors() > 0);
    }
    
    @Test
    public void testSyntaxError_MissingPipelineName() {
        String src = """
                pipeline {
                  every push on main
                  
                  stage build {
                    image "node:18"
                    run "npm install"
                  }
                }
                """;

        EduScriptParser parser = ParserUtils.getParser(src);
        parser.pipeline();
        
        assertTrue("Should have syntax errors", parser.getNumberOfSyntaxErrors() > 0);
    }
    
    @Test
    public void testSyntaxError_InvalidTriggerSyntax() {
        String src = """
                pipeline ci-ppl {
                  every push main  # Missing 'on' keyword
                  
                  stage build {
                    image "node:18"
                    run "npm install"
                  }
                }
                """;

        EduScriptParser parser = ParserUtils.getParser(src);
        parser.pipeline();
        
        assertTrue("Should have syntax errors", parser.getNumberOfSyntaxErrors() > 0);
    }
    
    @Test
    public void testSyntaxError_MissingStageName() {
        String src = """
                pipeline ci-ppl {
                  every push on main
                  
                  stage {  # Missing stage name
                    image "node:18"
                    run "npm install"
                  }
                }
                """;

        EduScriptParser parser = ParserUtils.getParser(src);
        parser.pipeline();
        
        assertTrue("Should have syntax errors", parser.getNumberOfSyntaxErrors() > 0);
    }
    
    @Test
    public void testSyntaxError_UnterminatedString() {
        String src = """
                pipeline ci-ppl {
                  every push on main
                  
                  stage build {
                    image "node:18
                    run "npm install"
                  }
                }
                """;

        EduScriptParser parser = ParserUtils.getParser(src);
        parser.pipeline();
        
        assertTrue("Should have syntax errors", parser.getNumberOfSyntaxErrors() > 0);
    }
    
    @Test
    public void testSyntaxError_InvalidKeyword() {
        String src = """
                pipeline ci-ppl {
                  every push on main
                  
                  stage build {
                    image "node:18"
                    execute "npm install"  # Invalid keyword, should be 'run'
                  }
                }
                """;

        EduScriptParser parser = ParserUtils.getParser(src);
        parser.pipeline();
        
        assertTrue("Should have syntax errors", parser.getNumberOfSyntaxErrors() > 0);
    }
    
    @Test
    public void testSyntaxError_MissingImageValue() {
        String src = """
                pipeline ci-ppl {
                  every push on main
                  
                  stage build {
                    image  # Missing image value
                    run "npm install"
                  }
                }
                """;

        EduScriptParser parser = ParserUtils.getParser(src);
        parser.pipeline();
        
        assertTrue("Should have syntax errors", parser.getNumberOfSyntaxErrors() > 0);
    }
    
    @Test
    public void testSyntaxError_InvalidEnvironmentSyntax() {
        String src = """
                pipeline ci-ppl {
                  env {
                    NODE_ENV "production"  # Missing '=' operator
                  }
                  
                  every push on main
                  
                  stage build {
                    image "node:18"
                    run "npm install"
                  }
                }
                """;

        EduScriptParser parser = ParserUtils.getParser(src);
        parser.pipeline();
        
        assertTrue("Should have syntax errors", parser.getNumberOfSyntaxErrors() > 0);
    }
    
    @Test
    public void testSyntaxError_InvalidConfigSyntax() {
        String src = """
                pipeline ci-ppl {
                  every push on main
                  
                  stage build {
                    image "node:18"
                    run "npm install"
                    config {
                      timeout 300  # Missing '=' operator
                    }
                  }
                }
                """;

        EduScriptParser parser = ParserUtils.getParser(src);
        parser.pipeline();
        
        assertTrue("Should have syntax errors", parser.getNumberOfSyntaxErrors() > 0);
    }
    
    @Test
    public void testSyntaxError_InvalidNeedsSyntax() {
        String src = """
                pipeline ci-ppl {
                  every push on main
                  
                  stage test {
                    image "node:18"
                    run "npm test"
                    needs build test  # Missing commas in list
                  }
                }
                """;

        EduScriptParser parser = ParserUtils.getParser(src);
        parser.pipeline();
        
        assertTrue("Should have syntax errors", parser.getNumberOfSyntaxErrors() > 0);
    }
    
    @Test
    public void testValidSyntax_ShouldNotHaveErrors() {
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
                    }
                  }
                  
                  stage test {
                    image "node:18"
                    run "npm test"
                    needs build
                  }
                }
                """;

        EduScriptParser parser = ParserUtils.getParser(src);
        parser.pipeline();
        
        assertEquals("Should not have syntax errors", 0, parser.getNumberOfSyntaxErrors());
    }
}
