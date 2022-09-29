package com.github.tamal8730.cotext.model;

public class DocumentModel {

    private String id;
    private String content;

    public DocumentModel() {
    }

    public DocumentModel(String id, String content) {
        this.id = id;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
