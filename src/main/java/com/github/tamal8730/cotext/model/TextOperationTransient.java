package com.github.tamal8730.cotext.model;


public class TextOperationTransient {
    private TextOperation operation;
    private int revision;
    private String from;

    public TextOperationTransient(TextOperation operation, int revision, String from) {
        this.operation = operation;
        this.revision = revision;
        this.from = from;
    }

    public TextOperationTransient() {
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
}
