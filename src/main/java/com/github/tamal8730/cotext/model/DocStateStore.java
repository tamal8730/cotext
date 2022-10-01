package com.github.tamal8730.cotext.model;

import com.github.tamal8730.cotext.document_formatter.DocumentFormatter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class DocStateStore {

    // DocId -> DocState
    private final Map<String, DocState> store = new HashMap<>();
    private final Supplier<DocumentFormatter> documentFormatterFactory;

    public DocStateStore(Supplier<DocumentFormatter> documentFormatterFactory) {
        this.documentFormatterFactory = documentFormatterFactory;
    }

    public DocState addEmptyDoc(String docId) {
        DocState newDocState = new DocState(documentFormatterFactory.get());
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
