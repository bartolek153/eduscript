package org.eduscript.configs.ws;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomHandshakeInterceptor implements HandshakeInterceptor {

    private static final String COOKIE_NAME = "uid";

    private String userIdAttribute;

    public void setUserIdAttribute(String userIdAttribute) {
        this.userIdAttribute = userIdAttribute;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {

        if (request instanceof ServletServerHttpRequest servletRequest
                && response instanceof ServletServerHttpResponse servletResponse) {

            HttpServletRequest httpRequest = servletRequest.getServletRequest();
            HttpServletResponse httpResponse = servletResponse.getServletResponse();

            String userId = null;
            Cookie[] cookies = httpRequest.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (COOKIE_NAME.equals(cookie.getName())) {
                        userId = cookie.getValue();
                        break;
                    }
                }
            }

            if (userId == null || userId.isEmpty()) {
                userId = UUID.randomUUID().toString();
                Cookie newCookie = new Cookie(COOKIE_NAME, userId);
                newCookie.setPath("/");
                // newCookie.setHttpOnly(false); // JS access
                newCookie.setMaxAge(60 * 60 * 24 * 7); // 7 days
                httpResponse.addCookie(newCookie);
            }

            attributes.put(userIdAttribute, userId);
        }

        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {
    }
}
