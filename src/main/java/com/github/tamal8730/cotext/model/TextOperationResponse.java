package com.github.tamal8730.cotext.model;

public class TextOperationResponse {

    private String acknowledgeTo;
    private TextOperation operation;
    private long revision;

    public TextOperationResponse(String acknowledgeTo, TextOperation operation, long revision) {
        this.acknowledgeTo = acknowledgeTo;
        this.operation = operation;
        this.revision = revision;
    }

    public TextOperationResponse() {
    }

    public String getAcknowledgeTo() {
        return acknowledgeTo;
    }

    public void setAcknowledgeTo(String acknowledgeTo) {
        this.acknowledgeTo = acknowledgeTo;
    }

    public TextOperation getOperation() {
        return operation;
    }

    public void setOperation(TextOperation operation) {
        this.operation = operation;
    }

    public long getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }
}
