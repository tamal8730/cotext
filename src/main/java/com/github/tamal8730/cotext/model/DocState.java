package com.github.tamal8730.cotext.model;

import com.github.tamal8730.cotext.document_formatter.DocumentFormatter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class DocState {

    @Autowired
    private DocumentFormatter documentFormatter;

    private int revision = 0;

    final List<TextOperation> revisionLog = new ArrayList<>();
    final Queue<TextOperationTransient> pendingOperations = new LinkedList<>();
    private String docText = "";

    public DocState() {
    }

    public List<TextOperation> getRevisionLog() {
        return revisionLog;
    }

    public int getRevision() {
        return revision;
    }

    public String getDocText() {
        return docText;
    }

    public void addPendingOperation(TextOperationTransient operation) {
        pendingOperations.add(operation);
    }

    public void applyCurrentOperation() {
        TextOperationTransient operation = pendingOperations.poll();
        if (operation == null) return;
        docText = documentFormatter.applyOperation(operation.getOperation());
        revisionLog.add(operation.getOperation());
        revision++;
    }

}
