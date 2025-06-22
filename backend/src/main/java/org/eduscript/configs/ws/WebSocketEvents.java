package org.eduscript.configs.ws;

import org.eduscript.services.UserSessionService;
import org.eduscript.utils.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

@Component
public class WebSocketEvents {

    private final UserSessionService userSessionService;

    @Value("${app.constants.user-id-attribute}")
    private String userIdAttribute;

    public WebSocketEvents(UserSessionService userSessionService) {
        this.userSessionService = userSessionService;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        userSessionService.saveSession(
                headerAccessor.getUser(),
                Utils.strToUUID(sessionId));
    }

    // @EventListener
    // public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    //     StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
    //     String sessionId = headerAccessor.getSessionId();

    //     logger.info("destroing a new web socket connection {}", sessionId);
    // }
}
