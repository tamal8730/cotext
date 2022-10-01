package com.github.tamal8730.cotext.controller;


import com.github.tamal8730.cotext.model.DocState;
import com.github.tamal8730.cotext.model.DocStateStore;
import com.github.tamal8730.cotext.model.KafkaMessageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class KafkaMessageListener {

    @Autowired
    private DocStateStore docStateStore;

    @KafkaListener(topics = "docs", containerFactory = "kafkaListenerContainerFactory")
    public void listener(KafkaMessageModel message) {
        DocState state = docStateStore.getDocState(message.getDocId());
        System.out.println("DOC " + message.getDocId());
    }

}
