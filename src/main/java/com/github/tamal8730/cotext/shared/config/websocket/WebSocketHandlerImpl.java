package com.github.tamal8730.cotext.shared.config.websocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class WebSocketHandlerImpl extends TextWebSocketHandler implements WebSocketHandler {

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.printf("[CLOSED] %s", session.getHandshakeHeaders());
    }

}
