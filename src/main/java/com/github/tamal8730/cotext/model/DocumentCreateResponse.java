package com.github.tamal8730.cotext.model;

import java.util.UUID;

public class DocumentCreateResponse {

    final public String userId;
    final public String docId;

    public DocumentCreateResponse(String docId) {
        this.userId = UUID.randomUUID().toString();
        this.docId = docId;
    }

}
