package com.github.tamal8730.cotext.shared.config.websocket;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class SessionDisconnectEventListener implements ApplicationListener<SessionDisconnectEvent> {

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        System.out.printf("[DISCONNECTED] %s\n", event);
    }

}
