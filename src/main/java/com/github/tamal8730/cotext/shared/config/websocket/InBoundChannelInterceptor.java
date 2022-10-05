package com.github.tamal8730.cotext.shared.config.websocket;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.util.MultiValueMap;

public class InBoundChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        System.out.println("Channel Interceptor");

        MessageHeaders headers = message.getHeaders();
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        MultiValueMap<String, String> multiValueMap = headers.get(StompHeaderAccessor.NATIVE_HEADERS, MultiValueMap.class);
        assert multiValueMap != null;
        multiValueMap.forEach((key, value) -> System.out.println(key + "#" + value));
        return message;
    }
}


