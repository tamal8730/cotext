package com.github.tamal8730.cotext.shared.operation_queue.impl;

import com.github.tamal8730.cotext.shared.model.OperationQueueInPayload;
import com.github.tamal8730.cotext.shared.operation_queue.OperationQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaOperationQueue implements OperationQueue {

    @Autowired
    private KafkaTemplate<String, OperationQueueInPayload> kafkaTemplate;

    @Override
    public void enqueue(OperationQueueInPayload inPayload) {
        kafkaTemplate.send("docs", inPayload);
    }

}
