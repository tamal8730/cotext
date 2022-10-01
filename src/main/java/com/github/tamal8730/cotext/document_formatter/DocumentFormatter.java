package com.github.tamal8730.cotext.document_formatter;

import com.github.tamal8730.cotext.model.TextOperation;

public interface DocumentFormatter {

    String applyOperation(TextOperation operation);
    String getText();

}
