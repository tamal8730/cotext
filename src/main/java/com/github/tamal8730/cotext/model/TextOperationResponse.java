package com.github.tamal8730.cotext.model;

public class TextOperationResponse {

    final String acknowledgeTo;
    final TextOperation operation;
    final int revision;

    public TextOperationResponse(String acknowledgeTo, TextOperation operation, int revision) {
        this.acknowledgeTo = acknowledgeTo;
        this.operation = operation;
        this.revision = revision;
    }
}
