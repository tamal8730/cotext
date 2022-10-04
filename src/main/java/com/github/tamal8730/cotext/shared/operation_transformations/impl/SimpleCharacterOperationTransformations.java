package com.github.tamal8730.cotext.shared.operation_transformations.impl;

import com.github.tamal8730.cotext.shared.model.TextOperation;
import com.github.tamal8730.cotext.shared.operation_transformations.OperationTransformations;

public class SimpleCharacterOperationTransformations implements OperationTransformations {

    @Override
    public TextOperation[] transform(TextOperation op1, TextOperation op2) {

        if (op1.getOpName().equals("ins") && op2.getOpName().equals("ins")) {
            return new TextOperation[]{transformII(op1, op2)};
        } else if (op1.getOpName().equals("ins") && op2.getOpName().equals("del")) {
            return new TextOperation[]{transformID(op1, op2)};
        } else if (op1.getOpName().equals("del") && op2.getOpName().equals("ins")) {
            return new TextOperation[]{transformDI(op1, op2)};
        } else if (op1.getOpName().equals("del") && op2.getOpName().equals("del")) {
            var transform = transformDD(op1, op2);
            return transform == null ? null : new TextOperation[]{transform};
        } else {
            return null;
        }

    }

    // insert-insert
    public TextOperation transformII(TextOperation op1, TextOperation op2) {
        if (op1.getPosition() < op2.getPosition()) {
            return new TextOperation(op1.getOpName(), op1.getOperand(), op1.getPosition());
        } else {
            return new TextOperation(op1.getOpName(), op1.getOperand(), op1.getPosition() + 1);
        }
    }

    // insert-delete
    public TextOperation transformID(TextOperation op1, TextOperation op2) {
        int newPos;
        if (op1.getPosition() <= op2.getPosition()) newPos = op1.getPosition();
        else newPos = op1.getPosition() - 1;
        return new TextOperation(op1.getOpName(), op1.getOperand(), newPos);
    }

    // delete-insert
    public TextOperation transformDI(TextOperation op1, TextOperation op2) {
        int newPos;
        if (op1.getPosition() < op2.getPosition()) newPos = op1.getPosition();
        else newPos = op1.getPosition() + 1;
        return new TextOperation(op1.getOpName(), op1.getOperand(), newPos);
    }

    // delete-delete
    public TextOperation transformDD(TextOperation op1, TextOperation op2) {
        int newPos;
        if (op1.getPosition() < op2.getPosition()) newPos = op1.getPosition();
        else if (op1.getPosition() > op2.getPosition()) newPos = op1.getPosition() - 1;
        else return null;
        return new TextOperation(op1.getOpName(), op1.getOperand(), newPos);
    }

}
