package com.github.tamal8730.cotext.feat_document.model;


public class DocumentJoinResponse {

    final public int collaboratorCount;
    final public boolean hasError;
    final public String errorMessage;
    final public String userId;
    final public String text;
    final public int documentRevision;

    private DocumentJoinResponse(int collaboratorCount, boolean hasError, String errorMessage, String userId, String text, int documentRevision) {
        this.collaboratorCount = collaboratorCount;
        this.hasError = hasError;
        this.errorMessage = errorMessage;
        this.userId = userId;
        this.text = text;
        this.documentRevision = documentRevision;
    }

    public static DocumentJoinResponse noError(int collaboratorCount, String userId, String text, int documentRevision) {
        return new DocumentJoinResponse(collaboratorCount, false, null, userId, text, documentRevision);
    }

    public static DocumentJoinResponse withError(String errorMessage) {
        return new DocumentJoinResponse(-1, true, errorMessage, null, null, -1);
    }

}
