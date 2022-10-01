package com.github.tamal8730.cotext.model;

import com.github.tamal8730.cotext.document_formatter.DocumentFormatter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class DocState {


    private final DocumentFormatter documentFormatter;

    private long revision = 0;

    final List<TextOperation> revisionLog = new ArrayList<>();
    final Queue<TextOperationTransient> pendingOperations = new LinkedList<>();

    public DocState(DocumentFormatter documentFormatter) {
        this.documentFormatter = documentFormatter;
    }

    public List<TextOperation> getRevisionLog() {
        return revisionLog;
    }

    public long getRevision() {
        return revision;
    }

    public String getDocText() {
        return documentFormatter.getText();
    }

    public void addPendingOperation(TextOperationTransient operation) {
        pendingOperations.add(operation);
    }

    public void applyCurrentOperation() {
        TextOperationTransient operation = pendingOperations.poll();
        if (operation == null) return;
        documentFormatter.applyOperation(operation.getOperation());
        revisionLog.add(operation.getOperation());
        revision++;
    }

}
