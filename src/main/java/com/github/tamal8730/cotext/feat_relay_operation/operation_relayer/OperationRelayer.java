package com.github.tamal8730.cotext.feat_relay_operation.operation_relayer;

import com.github.tamal8730.cotext.shared.model.OperationQueueOutPayload;

public interface OperationRelayer {
    void relay(String docId, OperationQueueOutPayload outPayload);
}