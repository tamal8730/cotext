package com.github.tamal8730.cotext.shared.operation_queue;

import com.github.tamal8730.cotext.shared.model.OperationQueueInPayload;

public interface OperationQueue {
    void enqueue(OperationQueueInPayload inPayload);
}
