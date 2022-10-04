package com.github.tamal8730.cotext.shared.operation_transformations;

import com.github.tamal8730.cotext.shared.model.TextOperation;

public interface OperationTransformations {
    TextOperation[] transform(TextOperation op1, TextOperation op2);
}
