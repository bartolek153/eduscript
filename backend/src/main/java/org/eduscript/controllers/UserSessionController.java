package org.eduscript.controllers;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

@Controller
public class UserSessionController {

    private static final String SIMP_SESSION_ID = "simpSessionId";
    
    @SubscribeMapping("/whoami")
    public String identifyUser(@Header(SIMP_SESSION_ID) String sessionId, Principal user) {
        return String.format("hsid=[%s],uid=[%s]", sessionId, user.getName());
    }
}
