package org.eduscript;

import org.eduscript.logging.Logger;
import org.eduscript.utils.AsyncLogger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
public class EduScriptWorker {
    public static void main(String[] args) {
        AsyncLogger asyncLogger = new AsyncLogger(5, 1000, true);
        Logger.addHandler(asyncLogger);
        asyncLogger.start();
        
        SpringApplication.run(EduScriptWorker.class, args);
    }
}
