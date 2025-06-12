package org.eduscript.controllers;

import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
public class LogController {

    @MessageMapping("/whoami")
    @SendToUser("/info/session")
    public String getSessionId(@Header("simpSessionId") String sessionId) {
        return sessionId;
    }
}
