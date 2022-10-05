package com.github.tamal8730.cotext.shared.message_pusher;

import com.github.tamal8730.cotext.shared.model.MessageOutPayloadWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessagePusher {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public void push(String type, String docId, Object payload) {
        simpMessagingTemplate.convertAndSend("/topic/doc/" + docId, new MessageOutPayloadWrapper<>(type, payload));
    }

}
