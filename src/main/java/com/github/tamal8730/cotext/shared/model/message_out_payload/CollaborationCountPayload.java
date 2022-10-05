package com.github.tamal8730.cotext.shared.model.message_out_payload;

public class CollaborationCountPayload {
    private int count;

    public CollaborationCountPayload(int count) {
        this.count = count;
    }

    public CollaborationCountPayload() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
