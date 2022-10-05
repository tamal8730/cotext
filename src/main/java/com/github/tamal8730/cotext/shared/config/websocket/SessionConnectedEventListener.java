package com.github.tamal8730.cotext.shared.config.websocket;

import com.github.tamal8730.cotext.shared.document_store.DocumentStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

import java.util.Objects;

@Component
public class SessionConnectedEventListener implements ApplicationListener<SessionConnectedEvent> {

    @Autowired
    public DocumentStore documentStore;

    @Override
    public void onApplicationEvent(SessionConnectedEvent event) {
        String sessionId = Objects.requireNonNull(event.getMessage().getHeaders().get("simpSessionId")).toString();
        if (sessionId != null) {

        }
    }

}
