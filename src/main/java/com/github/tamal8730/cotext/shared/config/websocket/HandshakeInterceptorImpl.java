package com.github.tamal8730.cotext.shared.config.websocket;

import com.github.tamal8730.cotext.shared.document_store.DocumentStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class HandshakeInterceptorImpl implements HandshakeInterceptor {

    private final DocumentStore documentStore;

    public HandshakeInterceptorImpl(DocumentStore documentStore) {
        this.documentStore = documentStore;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        var q = request.getURI().getQuery();
        if (q == null || q.isBlank() || q.isEmpty()) return true;
        String[] parts = q.split("=");
        if (parts.length != 2 || !parts[0].equals("id")) {
            response.setStatusCode(HttpStatus.NOT_FOUND);
            response.close();
            return false;
        }
        var hasDoc = documentStore.hasDocument(parts[1]);
        if (!hasDoc) {
            response.setStatusCode(HttpStatus.NOT_FOUND);
            response.close();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
