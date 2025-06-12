package org.eduscript.utils;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class AutoAssignId implements CommandLineRunner {
    
    @Override
    public void run(String... args) throws Exception {
            
    }
}
