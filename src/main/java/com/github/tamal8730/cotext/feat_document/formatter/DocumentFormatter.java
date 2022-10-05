package com.github.tamal8730.cotext.feat_document.formatter;

import com.github.tamal8730.cotext.shared.model.TextOperation;

public interface DocumentFormatter {

    String applyOperation(TextOperation operation);

    String getText();

}
