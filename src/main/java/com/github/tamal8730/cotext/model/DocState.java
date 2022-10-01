package com.github.tamal8730.cotext.model;

import com.github.tamal8730.cotext.document_formatter.DocumentFormatter;

import java.util.*;

public class DocState {


    private final DocumentFormatter documentFormatter;

    private int revision = 0;

    final Map<Integer, TextOperation> revisionLog = new HashMap<>();
//    final Queue<TextOperationTransient> pendingOperations = new LinkedList<>();

    public DocState(DocumentFormatter documentFormatter) {
        this.documentFormatter = documentFormatter;
    }

    public Map<Integer, TextOperation> getRevisionLog() {
        return revisionLog;
    }

    public int getRevision() {
        return revision;
    }

    public String getDocText() {
        return documentFormatter.getText();
    }

//    public void addPendingOperation(TextOperationTransient operation) {
//        pendingOperations.add(operation);
//    }

    public void applyCurrentOperation(TextOperation operation) {
//        TextOperationTransient operation = pendingOperations.poll();
//        if (operation == null) return;
        documentFormatter.applyOperation(operation);
        revisionLog.put(revision, operation);
//        revisionLog.add(operation);
        revision++;
    }

    public void removeCurrentOperationAndApplyTransformedOperation(TextOperation transformedOperation) {
//        TextOperationTransient operation = pendingOperations.poll();
//        if (operation == null) return;
        documentFormatter.applyOperation(transformedOperation);
        revisionLog.put(revision, transformedOperation);
//        revisionLog.add(transformedOperation);
        revision++;
    }

    public TextOperation applyTransformationsFrom(TextOperation operation, int from) {

        System.out.println("TRANS_FROM " + from + " " + revisionLog+", "+operation);

        TextOperation transformedOperation = operation;
        for (int i = from; i < revisionLog.size(); i++) {
            if (transformedOperation == null) continue;
            transformedOperation = transform(transformedOperation, revisionLog.get(i));
        }
        return transformedOperation;
    }

    private static TextOperation transform(TextOperation op1, TextOperation op2) {
        if (op1.getOpName().equals("ins") && op2.getOpName().equals("ins")) {
            return transformII(op1, op2);
        } else if (op1.getOpName().equals("ins") && op2.getOpName().equals("del")) {
            return transformID(op1, op2);
        } else if (op1.getOpName().equals("del") && op2.getOpName().equals("ins")) {
            return transformDI(op1, op2);
        } else if (op1.getOpName().equals("del") && op2.getOpName().equals("del")) {
            return transformDD(op1, op2);
        } else {
            return null;
        }
    }

    // insert-insert transform
    private static TextOperation transformII(TextOperation op1, TextOperation op2) {
        if (op1.getPosition() < op2.getPosition()) {
            return new TextOperation(op1.getOpName(), op1.getOperand(), op1.getPosition());
        } else {
            return new TextOperation(op1.getOpName(), op1.getOperand(), op1.getPosition() + 1);
        }
    }


    // insert-delete
    private static TextOperation transformID(TextOperation op1, TextOperation op2) {
        int newPos;
        if (op1.getPosition() <= op2.getPosition()) newPos = op1.getPosition();
        else newPos = op1.getPosition() - 1;
        return new TextOperation(op1.getOpName(), op1.getOperand(), newPos);
    }

    // delete-insert
    private static TextOperation transformDI(TextOperation op1, TextOperation op2) {
        int newPos;
        if (op1.getPosition() < op2.getPosition()) newPos = op1.getPosition();
        else newPos = op1.getPosition() + 1;
        return new TextOperation(op1.getOpName(), op1.getOperand(), newPos);
    }

    // delete-delete
    private static TextOperation transformDD(TextOperation op1, TextOperation op2) {
        int newPos;
        if (op1.getPosition() < op2.getPosition()) newPos = op1.getPosition();
        else if (op1.getPosition() > op2.getPosition()) newPos = op1.getPosition() - 1;
        else return null;
        return new TextOperation(op1.getOpName(), op1.getOperand(), newPos);
    }

}
