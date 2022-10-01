package com.github.tamal8730.cotext.document_formatter.impl;

import com.github.tamal8730.cotext.document_formatter.DocumentFormatter;
import com.github.tamal8730.cotext.model.TextOperation;

public class DocumentFormatterImpl implements DocumentFormatter {

    private final StringBuffer buffer = new StringBuffer();

    @Override
    public String applyOperation(TextOperation operation) {
        switch (operation.getOpName()) {
            case "ins":
                return applyInsert(operation);
            case "del":
                return applyRemoveChar(operation);
            default:
                return "";
        }
    }

    @Override
    public String getText() {
        return buffer.toString();
    }

    private String applyInsert(TextOperation operation) {
        System.out.println("OP " + operation + ", TEXT " + buffer);
        if (buffer.length() == operation.getPosition()) {
            buffer.append(operation.getOperand());
        } else {
            buffer.insert(operation.getPosition(), operation.getOperand());
        }
        return buffer.toString();
    }

    private String applyRemoveChar(TextOperation operation) {
        System.out.println("OP " + operation + ", TEXT " + buffer);
        buffer.deleteCharAt(operation.getPosition());
        return buffer.toString();
    }

}
