package org.eduscript.configs.dsl;

import org.eduscript.semantic.SemanticAnalyzer;
import org.eduscript.semantic.SemanticErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DslConfig {
    @Bean
    SemanticAnalyzer semanticAnalyzer(SemanticErrorHandler handler) {
        return new SemanticAnalyzer(handler);
    }

    @Bean
    SemanticErrorHandler semanticErrorHandler() {
        return new SemanticErrorHandler();
    }
}
