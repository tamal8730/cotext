package com.github.tamal8730.cotext.shared.model;

import com.github.tamal8730.cotext.feat_document.formatter.DocumentFormatter;
import com.github.tamal8730.cotext.shared.operation_transformations.OperationTransformations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class DocumentModel {

    private final OperationTransformations operationTransformations;

    private final DocumentFormatter documentFormatter;
    private int revision = 0;
    private int collaboratorCount = 0;

    final List<TextOperation> revisionLog = new ArrayList<>();

    public DocumentModel(DocumentFormatter documentFormatter, OperationTransformations operationTransformations) {
        this.documentFormatter = documentFormatter;
        this.operationTransformations = operationTransformations;
    }

    public int getRevision() {
        return revision;
    }

    public String getDocText() {
        return documentFormatter.getText();
    }

    public void applyOperation(TextOperation operation) {
        documentFormatter.applyOperation(operation);
        revisionLog.add(revision, operation);
        revision++;
    }

    public TextOperation applyTransformationsAgainstRevisionLogsFrom(TextOperation operation, int from) {

        TextOperation transformedOperation = operation;
        for (int i = from; i < revisionLog.size(); i++) {
            if (transformedOperation == null) return null;

            var operations = operationTransformations.transform(transformedOperation, revisionLog.get(i));
            // TODO:
            if (operations == null) {
                return null;
            }
            transformedOperation = operations[0];
        }
        return transformedOperation;

    }

    public int getCollaboratorCount() {
        return collaboratorCount;
    }

}
