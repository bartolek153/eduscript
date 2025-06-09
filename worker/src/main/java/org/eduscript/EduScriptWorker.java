package org.eduscript;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
public class EduScriptWorker {
    public static void main(String[] args) {
        SpringApplication.run(EduScriptWorker.class, args);
    }
}
