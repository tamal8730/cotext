package com.github.tamal8730.cotext.shared.model;

import com.github.tamal8730.cotext.feat_document.formatter.DocumentFormatter;
import com.github.tamal8730.cotext.shared.operation_transformations.OperationTransformations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class DocumentModel {

    @Autowired
    private OperationTransformations operationTransformations;

    private final DocumentFormatter documentFormatter;
    private int revision = 0;

    final Map<Integer, TextOperation> revisionLog = new HashMap<>();

    public DocumentModel(DocumentFormatter documentFormatter) {
        this.documentFormatter = documentFormatter;
    }


    public int getRevision() {
        return revision;
    }

    public String getDocText() {
        return documentFormatter.getText();
    }

    public void applyOperation(TextOperation operation) {
        documentFormatter.applyOperation(operation);
        revisionLog.put(revision, operation);
        revision++;
    }

    public TextOperation applyTransformationsAgainstRevisionLogsFrom(TextOperation operation, int from) {

        TextOperation transformedOperation = operation;
        for (int i = from; i < revisionLog.size(); i++) {
            if (transformedOperation == null) return null;
            transformedOperation = operationTransformations.transform(transformedOperation, revisionLog.get(i));
        }
        return transformedOperation;

    }


}
