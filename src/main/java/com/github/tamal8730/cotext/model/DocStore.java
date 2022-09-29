package com.github.tamal8730.cotext.model;

import java.util.HashMap;
import java.util.Map;

public class DocStore {
    private Map<String, DocumentModel> store = new HashMap<>();

    public DocumentModel addDoc(String id) {
        DocumentModel model = new DocumentModel(id, "");
        store.put(id, model);
        return model;
    }

    public DocumentModel getDoc(String id) {
        return store.get(id);
    }

    public void removeDoc(String id) {
        store.remove(id);
    }

}
