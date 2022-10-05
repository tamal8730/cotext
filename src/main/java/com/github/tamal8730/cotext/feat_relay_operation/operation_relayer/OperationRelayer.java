package com.github.tamal8730.cotext.feat_relay_operation.operation_relayer;

import com.github.tamal8730.cotext.shared.message_pusher.MessagePusher;
import com.github.tamal8730.cotext.shared.model.message_out_payload.OperationQueueOutPayload;
import org.springframework.beans.factory.annotation.Autowired;

public class OperationRelayer {

    @Autowired
    public MessagePusher messageRelayer;

    public void relay(String docId, OperationQueueOutPayload outPayload) {
        messageRelayer.push("operation", docId, outPayload);
    }

}