package com.github.tamal8730.cotext.model;

public class KafkaMessageModel {

    private String docId;
    private TextOperationTransient textOperationTransient;

    public KafkaMessageModel(String docId, TextOperationTransient textOperationTransient) {
        this.docId = docId;
        this.textOperationTransient = textOperationTransient;
    }

    public KafkaMessageModel() {
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public TextOperationTransient getTextOperationTransient() {
        return textOperationTransient;
    }

    public void setTextOperationTransient(TextOperationTransient textOperationTransient) {
        this.textOperationTransient = textOperationTransient;
    }
}
