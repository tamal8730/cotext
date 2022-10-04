package com.github.tamal8730.cotext.shared.operation_transformations.impl;

import com.github.tamal8730.cotext.shared.model.TextOperation;
import com.github.tamal8730.cotext.shared.operation_transformations.OperationTransformations;

public class CharSequenceOperationTransformations implements OperationTransformations {

    @Override
    public TextOperation[] transform(TextOperation op1, TextOperation op2) {
        return transformOperation(op1, op2);
    }

    private TextOperation[] transformOperation(TextOperation op1, TextOperation op2) {

        var op1Name = op1.getOpName();
        var op2Name = op2.getOpName();

        if (op1Name.equals("ins") && op2Name.equals("ins"))
            return new TextOperation[]{transformII(op1, op2)};
        else if (op1Name.equals("ins") && op2Name.equals("del"))
            return new TextOperation[]{transformID(op1, op2)};
        else if (op1Name.equals("del") && op2Name.equals("ins"))
            return transformDI(op1, op2);
        else if (op1Name.equals("del") && op2Name.equals("del"))
            return new TextOperation[]{transformDD(op1, op2)};
        else return null;
    }

    // insert-insert transform
    private TextOperation transformII(TextOperation op1, TextOperation op2) {
        var newPos = 0;
        if (op1.getPosition() < op2.getPosition()) {
            newPos = op1.getPosition();
        } else {
            newPos = op1.getPosition() + op2.getOperand().length();
        }
        return new TextOperation(op1.getOpName(), op1.getOperand(), newPos);
    }

    // insert-delete
    private TextOperation transformID(TextOperation op1, TextOperation op2) {
        var op2End = op2.getPosition() + op2.getOperand().length() - 1;
        if (op1.getPosition() <= op2.getPosition()) {
            return new TextOperation(
                    op1.getOpName(),
                    op1.getOperand(),
                    op1.getPosition()
            );
        } else if (op1.getPosition() > op2.getPosition() && op1.getPosition() <= op2End) {
            return new TextOperation(
                    op1.getOpName(),
                    op1.getOperand(),
                    op2.getPosition()
            );
        } else {
            return new TextOperation(
                    op1.getOpName(),
                    op1.getOperand(),
                    op1.getPosition() - op2.getOperand().length()
            );
        }
    }

    // delete-insert
    private TextOperation[] transformDI(TextOperation op1, TextOperation op2) {
        var op1End = op1.getPosition() + op1.getOperand().length() - 1;
        if (op1.getPosition() < op2.getPosition()) {
            if (op1End < op2.getPosition()) {
                return new TextOperation[]{
                        new TextOperation(
                                op1.getOpName(),
                                op1.getOperand(),
                                op1.getPosition()
                        )
                };
            } else {
                var left = op1.getOperand().substring(0, op2.getPosition() - op1.getPosition());
                var right = op1.getOperand().substring(left.length());

                // two operations
                return new TextOperation[]{
                        new TextOperation(op1.getOpName(), left, op1.getPosition()),
                        new TextOperation(
                                op1.getOpName(),
                                right,
                                op1.getPosition() + left.length() + op2.getOperand().length()
                        )
                };
            }
        } else {
            return new TextOperation[]{new TextOperation(
                    op1.getOpName(),
                    op1.getOperand(),
                    op1.getPosition() + op2.getOperand().length()
            )};
        }
    }

    // delete-delete
    private TextOperation transformDD(TextOperation op1, TextOperation op2) {
        var op1End = op1.getPosition() + op1.getOperand().length() - 1;
        var op2End = op2.getPosition() + op2.getOperand().length() - 1;

        if (op1End < op2.getPosition()) {
            return new TextOperation(
                    op1.getOpName(),
                    op1.getOperand(),
                    op1.getPosition()
            );
        } else if (op1.getPosition() > op2End) {
            return new TextOperation(
                    op1.getOpName(),
                    op1.getOperand(),
                    op1.getPosition() - op2.getOperand().length()
            );
        } else if (op1.getPosition() < op2.getPosition() && op1End >= op2.getPosition()) {
            var diff = op2.getPosition() - op1.getPosition();
            var operand = op1.getOperand().substring(0, diff);
            return new TextOperation(op1.getOpName(), operand, op1.getPosition());
        } else if (op1.getPosition() <= op2End && op1End > op2End) {
            var diff =
                    op1.getPosition() + op1.getOperand().length() - (op2.getPosition() + op2.getOperand().length());
            var operand = op1.getOperand().substring(op1.getOperand().length() - diff);
            return new TextOperation(op1.getOpName(), operand, op2.getPosition());
        } else {
            return null;
        }
    }

}
