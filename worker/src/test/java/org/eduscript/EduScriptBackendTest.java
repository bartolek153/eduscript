package org.eduscript;

import static org.junit.Assert.*;

import org.eduscript.logging.Logger;
import org.eduscript.utils.AsyncLogger;
import org.junit.*;

public class EduScriptBackendTest {

    @Test
    public void testAsyncLogging() throws InterruptedException {
        AsyncLogger as = new AsyncLogger(100000, 5000, false);
        
        int i = 0;
        Logger.addHandler(as);
        as.start();

        while (i < 10000) {
            if (i % 10 == 0) {
                Thread.sleep(3000);
            }

            Logger.printInfo("teste " + i);
            i++;
        }
    }
}
