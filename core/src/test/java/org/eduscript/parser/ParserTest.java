package org.eduscript.parser;

import static org.junit.Assert.*;

import org.eduscript.utils.ParserUtils;
import org.junit.Test;

import main.antlr4.EduScriptParser;

public class ParserTest {

  @Test
  public void testParseSimplePipeline() {
    String src = """
        pipeline simple {
          every push on main
          stage test {
            image "alpine"
            run "echo hello"
          }
        }
        """;

    var parser = ParserUtils.getParser(src);
    var tree = parser.pipeline();

    assertEquals(0, parser.getNumberOfSyntaxErrors());
    assertNotNull(tree);
  }

  @Test
  public void testParsePipeline_WithEnvironments() {
    String src = """
        pipeline env-test {
          env {
            NODE_ENV = "production"
            API_URL = "https://api.example.com"
          }

          every push on main

          stage build {
            image "node:18"
            run "npm install"
          }
        }
        """;

    var parser = ParserUtils.getParser(src);
    var tree = parser.pipeline();

    assertEquals(0, parser.getNumberOfSyntaxErrors());
    assertNotNull(tree);
  }

  @Test
  public void testParsePipeline_WithMultipleStages() {
    String src = """
        pipeline multi-stage {
          every push on main

          stage build {
            image "node:18"
            run "npm install"
            run "npm run build"
          }

          stage test {
            image "node:18"
            run "npm test"
          }

          stage deploy {
            image "alpine:latest"
            run "echo deploying"
          }
        }
        """;

    var parser = ParserUtils.getParser(src);
    var tree = parser.pipeline();

    assertEquals(0, parser.getNumberOfSyntaxErrors());
    assertNotNull(tree);
  }

  @Test
  public void testParsePipeline_WithDifferentTriggers() {
    // Test PUSH trigger
    String pushSrc = """
        pipeline push-test {
          every push on develop
          stage build {
            image "alpine"
            run "echo push"
          }
        }
        """;

    var pushParser = ParserUtils.getParser(pushSrc);
    pushParser.pipeline();
    assertEquals(0, pushParser.getNumberOfSyntaxErrors());

    // Test PULL_REQUEST trigger
    String prSrc = """
        pipeline pr-test {
          every pull_request on main
          stage test {
            image "alpine"
            run "echo pr"
          }
        }
        """;

    var prParser = ParserUtils.getParser(prSrc);
    prParser.pipeline();
    assertEquals(0, prParser.getNumberOfSyntaxErrors());

    // Test TAG trigger
    String tagSrc = """
        pipeline tag-test {
          every tag on "v*"
          stage deploy {
            image "alpine"
            run "echo tag"
          }
        }
        """;

    var tagParser = ParserUtils.getParser(tagSrc);
    tagParser.pipeline();
    assertEquals(0, tagParser.getNumberOfSyntaxErrors());
  }

  @Test
  public void testParsePipeline_WithStageConfigs() {
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

    var parser = ParserUtils.getParser(src);
    var tree = parser.pipeline();

    assertEquals(0, parser.getNumberOfSyntaxErrors());
    assertNotNull(tree);
  }

  @Test
  public void testParsePipeline_WithStageDependencies() {
    String src = """
        pipeline deps-test {
          every push on main

          stage build {
            image "node:18"
            run "npm install"
          }

          stage test {
            image "node:18"
            run "npm test"
            needs build
          }

          stage deploy {
            image "alpine:latest"
            run "echo deploying"
            needs build, test
          }
        }
        """;

    var parser = ParserUtils.getParser(src);
    var tree = parser.pipeline();

    assertEquals(0, parser.getNumberOfSyntaxErrors());
    assertNotNull(tree);
  }

  @Test
  public void testParsePipeline_WithMultilineRunCommand() {
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

    var parser = ParserUtils.getParser(src);
    var tree = parser.pipeline();

    assertEquals(0, parser.getNumberOfSyntaxErrors());
    assertNotNull(tree);
  }

  @Test
  public void testParsePipeline_WithComments() {
    String src = """
        # This is a test pipeline
        pipeline comment-test {
          # Environment variables
          env {
            NODE_ENV = "production"  # Production environment
          }

          # Trigger on push to main
          every push on main

          # Build stage
          stage build {
            image "node:18"  # Use Node.js 18
            run "npm install"  # Install dependencies
            run "npm run build"  # Build the project
          }
        }
        """;

    var parser = ParserUtils.getParser(src);
    var tree = parser.pipeline();

    assertEquals(0, parser.getNumberOfSyntaxErrors());
    assertNotNull(tree);
  }

  @Test
  public void testParsePipeline_WithQuotedStrings() {
    String src = """
        pipeline quoted-test {
          every push on "feature/test-branch"

          stage build {
            image "node:18-alpine"
            run "echo 'Hello World'"
            run "npm run test -- --reporter=json"
          }
        }
        """;

    var parser = ParserUtils.getParser(src);
    var tree = parser.pipeline();

    assertEquals(0, parser.getNumberOfSyntaxErrors());
    assertNotNull(tree);
  }

  @Test
  public void testParsePipeline_WithIdentifiers() {
    String src = """
        pipeline identifier-test {
          env {
            MY_VAR = my_value
            ANOTHER_VAR = another-value
          }

          every push on main

          stage build {
            image node:18
            run "npm install"
          }
        }
        """;

    var parser = ParserUtils.getParser(src);
    var tree = parser.pipeline();

    assertEquals(0, parser.getNumberOfSyntaxErrors());
    assertNotNull(tree);
  }

  @Test
  public void testParseComplexPipeline() {
    String src = """
        # Complex CI/CD Pipeline
        pipeline complex-ci {
          env {
            NODE_ENV = "production"
            API_URL = "https://api.example.com"
            DATABASE_URL = "postgresql://localhost:5432/mydb"
          }

          every push on main

          stage build {
            image "node:18-alpine"
            run "npm ci"
            run "npm run build"
            run "npm run test:unit"
            config {
              timeout = "600"
              memory = "1g"
            }
          }

          stage test {
            image "node:18-alpine"
            run "npm run test:integration"
            run "npm run test:e2e"
            needs build
            config {
              parallel = "true"
              timeout = "900"
            }
          }

          stage lint {
            image "node:18-alpine"
            run "npm run lint"
            run "npm run format:check"
            needs build
          }

          stage security {
            image "node:18-alpine"
            run "npm audit"
            run "npm run security:check"
            needs build
          }

          stage deploy {
            image "alpine:latest"
            run \"\"\"
            echo "Deploying to production"
            kubectl apply -f k8s/
            kubectl rollout status deployment/myapp
            \"\"\"
            needs build, test, lint, security
            config {
              environment = "production"
              timeout = "1200"
              retry = "2"
            }
          }
        }
        """;

    var parser = ParserUtils.getParser(src);
    var tree = parser.pipeline();

    assertEquals(0, parser.getNumberOfSyntaxErrors());
    assertNotNull(tree);
  }
}
