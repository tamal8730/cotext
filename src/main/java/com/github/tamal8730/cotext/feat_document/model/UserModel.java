package com.github.tamal8730.cotext.feat_document.model;

public class UserModel {

    private String userId;

    public UserModel(String userId) {
        this.userId = userId;
    }

    public UserModel() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
