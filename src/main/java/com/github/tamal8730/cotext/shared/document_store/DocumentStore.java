package com.github.tamal8730.cotext.shared.document_store;

import com.github.tamal8730.cotext.feat_document.formatter.DocumentFormatter;
import com.github.tamal8730.cotext.shared.model.DocumentModel;
import com.github.tamal8730.cotext.shared.operation_transformations.OperationTransformations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Supplier;


public abstract class DocumentStore {

    @Autowired
    private OperationTransformations operationTransformations;

    public OperationTransformations getOperationTransformations() {
        return operationTransformations;
    }

    final public Supplier<DocumentFormatter> documentFormatterFactory;

    public DocumentStore(Supplier<DocumentFormatter> documentFormatterFactory) {
        this.documentFormatterFactory = documentFormatterFactory;
    }

    public abstract DocumentModel addEmptyDocument(String userId, String docId);

    public abstract DocumentModel getDocumentFromDocId(String docId);
    public abstract DocumentModel getDocumentFromUserId(String docId);

    public abstract DocumentModel removeDocument(String docId);

    public abstract void addCollaboratorToDocument(String userId, String docId);
    public abstract DocumentModel removeCollaboratorFromDocument(String userId);

}
