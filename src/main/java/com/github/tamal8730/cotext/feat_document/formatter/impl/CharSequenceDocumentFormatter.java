package com.github.tamal8730.cotext.feat_document.formatter.impl;

import com.github.tamal8730.cotext.feat_document.formatter.DocumentFormatter;
import com.github.tamal8730.cotext.shared.model.TextOperation;

public class CharSequenceDocumentFormatter implements DocumentFormatter {

    private final StringBuffer buffer = new StringBuffer();

    @Override
    public String applyOperation(TextOperation operation) {
        switch (operation.getOpName()) {
            case "ins":
                return applyInsert(operation);
            case "del":
                return applyDelete(operation);
            default:
                return "";
        }
    }

    @Override
    public String getText() {
        return buffer.toString();
    }

    private String applyInsert(TextOperation operation) {
        if (buffer.length() == operation.getPosition()) {
            buffer.append(operation.getOperand());
        } else {
            buffer.insert(operation.getPosition(), operation.getOperand());
        }
        return buffer.toString();
    }

    private String applyDelete(TextOperation operation) {
        var start = operation.getPosition();
        var end = start + operation.getOperand().length();
        buffer.delete(start, end);
        return buffer.toString();
    }

}
