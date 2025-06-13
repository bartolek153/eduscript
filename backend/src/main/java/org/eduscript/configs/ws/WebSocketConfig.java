package org.eduscript.configs.ws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${app.constants.user-id-attribute}")
    private String userIdAttribute;
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // STOMP messages whose destination header begins with /app are routed to
        // @MessageMapping methods in @Controller classes
        config.setApplicationDestinationPrefixes("/app"); // basically = ws client produces

        // Use the built-in message broker for subscriptions and broadcasting and
        // route messages whose destination header begins with /topic or /queue to the
        // broker
        config.enableSimpleBroker("/topic", "/queue"); // basically = ws client listens
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // /ws is the HTTP URL for the endpoint to which a WebSocket (or SockJS)
        // client needs to connect for the WebSocket handshake
        CustomHandshakeInterceptor cusHInterc = new CustomHandshakeInterceptor();
        CustomHandshakeHandler cusHHandler = new CustomHandshakeHandler();
        
        cusHInterc.setUserIdAttribute(userIdAttribute);
        cusHHandler.setUserIdAttribute(userIdAttribute);
        
        registry
                .addEndpoint("/ws")
                .addInterceptors(cusHInterc)
                .setHandshakeHandler(cusHHandler)
                .setAllowedOrigins("*");
   }
}
