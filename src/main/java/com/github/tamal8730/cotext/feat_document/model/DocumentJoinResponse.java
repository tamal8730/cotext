package com.github.tamal8730.cotext.feat_document.model;


public class DocumentJoinResponse {

    final public boolean hasError;
    final public String errorMessage;
    final public String userId;
    final public String text;

    private DocumentJoinResponse(boolean hasError, String errorMessage, String userId, String text) {
        this.hasError = hasError;
        this.errorMessage = errorMessage;
        this.userId = userId;
        this.text = text;
    }

    public static DocumentJoinResponse noError(String userId, String text) {
        return new DocumentJoinResponse(false, null, userId, text);
    }

    public static DocumentJoinResponse withError(String errorMessage) {
        return new DocumentJoinResponse(true, errorMessage, null, null);
    }

}
