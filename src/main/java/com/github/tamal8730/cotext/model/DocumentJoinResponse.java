package com.github.tamal8730.cotext.model;

import java.util.UUID;

public class DocumentJoinResponse {

    final public String userId;
    final public String text;

    public DocumentJoinResponse(String text) {
        this.userId = UUID.randomUUID().toString();
        this.text = text;
    }

}
