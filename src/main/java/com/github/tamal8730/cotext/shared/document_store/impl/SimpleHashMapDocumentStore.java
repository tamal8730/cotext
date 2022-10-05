package com.github.tamal8730.cotext.shared.document_store.impl;

import com.github.tamal8730.cotext.feat_document.formatter.DocumentFormatter;
import com.github.tamal8730.cotext.shared.document_store.DocumentStore;
import com.github.tamal8730.cotext.shared.model.DocumentModel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class SimpleHashMapDocumentStore extends DocumentStore {

    // DocId -> DocState
    private final ConcurrentHashMap<String, DocumentModel> store = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> userIdToDocIdMap = new ConcurrentHashMap<>();

    public SimpleHashMapDocumentStore(Supplier<DocumentFormatter> documentFormatterFactory) {
        super(documentFormatterFactory);
    }

    @Override
    public String toString() {
        return "SimpleHashMapDocumentStore{" +
                "store=" + store +
                ", userIdToDocIdMap=" + userIdToDocIdMap +
                '}';
    }

    @Override
    public DocumentModel addEmptyDocument(String userId, String docId) {
        DocumentModel newDocState = new DocumentModel(docId, documentFormatterFactory.get(), getOperationTransformations());
        store.put(docId, newDocState);
        addCollaboratorToDocument(userId, docId);
        return newDocState;
    }

    @Override
    public DocumentModel getDocumentFromDocId(String docId) {
        return store.get(docId);
    }

    @Override
    public DocumentModel removeDocument(String docId) {
        return store.remove(docId);
    }

    @Override
    public void addCollaboratorToDocument(String userId, String docId) {
        userIdToDocIdMap.put(userId, docId);
        getDocumentFromDocId(docId).incrementCollaboratorCount();
    }

    @Override
    public DocumentModel removeCollaboratorFromDocument(String userId) {
        var doc = getDocumentFromUserId(userId);
        int newCount = doc.decrementCollaboratorCount();
        if (newCount == 0) {
            removeDocument(doc.getId());
            System.out.printf("[REMOVED] Removed document %s", doc.getId());
        }
        return doc;
    }

    @Override
    public DocumentModel getDocumentFromUserId(String userId) {
        return store.get(userIdToDocIdMap.get(userId));
    }


}
