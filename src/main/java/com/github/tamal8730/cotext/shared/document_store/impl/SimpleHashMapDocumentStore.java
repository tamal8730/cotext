package com.github.tamal8730.cotext.shared.document_store.impl;

import com.github.tamal8730.cotext.feat_document.formatter.DocumentFormatter;
import com.github.tamal8730.cotext.shared.document_store.DocumentStore;
import com.github.tamal8730.cotext.shared.model.DocumentModel;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SimpleHashMapDocumentStore extends DocumentStore {

    // DocId -> DocState
    private final Map<String, DocumentModel> store = new HashMap<>();

    public SimpleHashMapDocumentStore(Supplier<DocumentFormatter> documentFormatterFactory) {
        super(documentFormatterFactory);
    }

    @Override
    public DocumentModel addEmptyDocument(String docId) {
        DocumentModel newDocState = new DocumentModel(docId, documentFormatterFactory.get(), getOperationTransformations());
        store.put(docId, newDocState);
        return newDocState;
    }

    @Override
    public DocumentModel getDocument(String docId) {
        return store.get(docId);
    }

    @Override
    public DocumentModel removeDocument(String docId) {
        return store.remove(docId);
    }
}
