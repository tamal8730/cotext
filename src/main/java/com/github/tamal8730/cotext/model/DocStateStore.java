package com.github.tamal8730.cotext.model;

import java.util.HashMap;
import java.util.Map;

public class DocStateStore {

    // DocId -> DocState
    private final Map<String, DocState> store = new HashMap<>();

    public DocState addEmptyDoc(String docId) {
        DocState newDocState = new DocState();
        store.put(docId, newDocState);
        return newDocState;
    }

    public DocState getDocState(String docId) {
        return store.get(docId);
    }

    public void removeDocState(String docId) {
        store.remove(docId);
    }
}
