package com.github.tamal8730.cotext.feat_enqueue_operation.model;

import com.github.tamal8730.cotext.shared.model.TextOperation;

public class EnqueueOperationPayload {

    private TextOperation operation;
    private int revision;
    private String from;

    public EnqueueOperationPayload(TextOperation operation, int revision, String from) {
        this.operation = operation;
        this.revision = revision;
        this.from = from;
    }

    public EnqueueOperationPayload() {
    }

    public TextOperation getOperation() {
        return operation;
    }

    public void setOperation(TextOperation operation) {
        this.operation = operation;
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    @Override
    public String toString() {
        return "EnqueueOperationPayload{" +
                "operation=" + operation +
                ", revision=" + revision +
                ", from='" + from + '\'' +
                '}';
    }
}
