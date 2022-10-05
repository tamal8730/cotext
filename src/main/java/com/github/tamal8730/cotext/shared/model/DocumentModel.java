package com.github.tamal8730.cotext.shared.model;

import com.github.tamal8730.cotext.feat_document.formatter.DocumentFormatter;
import com.github.tamal8730.cotext.shared.operation_transformations.OperationTransformations;

import java.util.*;

public class DocumentModel {

    private final OperationTransformations operationTransformations;

    private final String id;
    private final DocumentFormatter documentFormatter;
    private int revision = 0;
    private int collaboratorCount = 0;

    final List<TextOperation> revisionLog = new ArrayList<>();

    public DocumentModel(String id, DocumentFormatter documentFormatter, OperationTransformations operationTransformations) {
        this.id=id;
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
            if (operations == null) return null;
            transformedOperation = operations[0];
        }
        return transformedOperation;

    }

    public List<TextOperation> transformAgainstRevisionLogs(TextOperation operation, int from) {

        class TextOperationWrapper {

            final TextOperation operation;
            final int transformFrom;

            TextOperationWrapper(TextOperation operation, int transformFrom) {
                this.operation = operation;
                this.transformFrom = transformFrom;
            }
        }

        List<TextOperation> transformedOperations = new ArrayList<>();
        Queue<TextOperationWrapper> opQueue = new LinkedList<>();
        opQueue.add(new TextOperationWrapper(operation, from));

        while (!opQueue.isEmpty()) {

            var op = opQueue.poll();
            var transformedOperation = operation;

            for (int revision = op.transformFrom; revision < revisionLog.size(); revision++) {
                var operations = operationTransformations.transform(transformedOperation, revisionLog.get(revision));
                if (operations == null || operations.length == 0) {
                    transformedOperation = null;
                    break;
                }
                transformedOperation = operations[0];
                if (operations.length > 1) {
                    opQueue.add(new TextOperationWrapper(operations[1], revision + 1));
                }
            }

            transformedOperations.add(transformedOperation);

        }

        return transformedOperations;

    }

    public int getCollaboratorCount() {
        return collaboratorCount;
    }

    public int incrementCollaboratorCount() {
        collaboratorCount++;
        return collaboratorCount;
    }

    public int decrementCollaboratorCount(){
        collaboratorCount--;
        return collaboratorCount;
    }

    public String getId() {
        return id;
    }
}
