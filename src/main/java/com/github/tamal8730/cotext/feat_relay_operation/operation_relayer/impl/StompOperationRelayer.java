package com.github.tamal8730.cotext.feat_relay_operation.operation_relayer.impl;

import com.github.tamal8730.cotext.feat_relay_operation.operation_relayer.OperationRelayer;
import com.github.tamal8730.cotext.shared.model.OperationQueueOutPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class StompOperationRelayer implements OperationRelayer {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void relay(String docId, OperationQueueOutPayload outPayload) {
        simpMessagingTemplate.convertAndSend("/topic/doc/" + docId, outPayload);
    }

}
