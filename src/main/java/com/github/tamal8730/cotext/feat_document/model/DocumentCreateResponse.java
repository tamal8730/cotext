package com.github.tamal8730.cotext.feat_document.model;

import java.util.UUID;

public class DocumentCreateResponse {

    final public String userId;
    final public String docId;

    public DocumentCreateResponse(String userId, String docId) {
        this.userId = userId;
        this.docId = docId;
    }

}
