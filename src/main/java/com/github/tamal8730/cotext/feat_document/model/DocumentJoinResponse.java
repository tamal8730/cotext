package com.github.tamal8730.cotext.feat_document.model;


public class DocumentJoinResponse {

    final public boolean hasError;
    final public String errorMessage;
    final public String userId;
    final public String text;
    final public int documentRevision;

    private DocumentJoinResponse(boolean hasError, String errorMessage, String userId, String text, int documentRevision) {
        this.hasError = hasError;
        this.errorMessage = errorMessage;
        this.userId = userId;
        this.text = text;
        this.documentRevision = documentRevision;
    }

    public static DocumentJoinResponse noError(String userId, String text, int documentRevision) {
        return new DocumentJoinResponse(false, null, userId, text, documentRevision);
    }

    public static DocumentJoinResponse withError(String errorMessage) {
        return new DocumentJoinResponse(true, errorMessage, null, null, -1);
    }

}
