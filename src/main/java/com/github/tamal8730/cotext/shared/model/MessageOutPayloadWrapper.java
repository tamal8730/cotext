package com.github.tamal8730.cotext.shared.model;

public class MessageOutPayloadWrapper<P> {

    private String type;
    private P payload;

    public MessageOutPayloadWrapper(String type, P payload) {
        this.type = type;
        this.payload = payload;
    }

    public MessageOutPayloadWrapper() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public P getPayload() {
        return payload;
    }

    public void setPayload(P payload) {
        this.payload = payload;
    }

}
