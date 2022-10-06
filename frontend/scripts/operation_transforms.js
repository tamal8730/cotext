class TextOperation {
    constructor(opName, operand, position, revision) {
        this.opName = opName;
        this.operand = operand;
        this.position = position;
        this.revision = revision;
    }
}

class OperationTransformation {
    static transformOperation(op1, op2) {
        let op1Name = op1.opName;
        let op2Name = op2.opName;

        if (op1Name === 'ins' && op2Name === 'ins')
            return OperationTransformation.transformII(op1, op2);
        else if (op1Name === 'ins' && op2Name === 'del')
            return OperationTransformation.transformID(op1, op2);
        else if (op1Name === 'del' && op2Name === 'ins')
            return OperationTransformation.transformDI(op1, op2);
        else if (op1Name === 'del' && op2Name === 'del')
            return OperationTransformation.transformDD(op1, op2);
        else return null;
    }

    // insert-insert transform
    static transformII(op1, op2) {
        let newPos = 0;
        if (op1.position < op2.position) {
            newPos = op1.position;
        } else {
            newPos = op1.position + op2.operand.length;
        }
        return new TextOperation(op1.opName, op1.operand, newPos, op1.revision);
    }

    // insert-delete
    static transformID(op1, op2) {
        let op2End = op2.position + op2.operand.length - 1;
        if (op1.position <= op2.position) {
            return new TextOperation(
                op1.opName,
                op1.operand,
                op1.position,
                op1.revision
            );
        } else if (op1.position > op2.position && op1.position <= op2End) {
            return new TextOperation(
                op1.opName,
                op1.operand,
                op2.position,
                op1.revision
            );
        } else {
            return new TextOperation(
                op1.opName,
                op1.operand,
                op1.position - op2.operand.length,
                op1.revision
            );
        }
    }

    // delete-insert
    static transformDI(op1, op2) {
        let op1End = op1.position + op1.operand.length - 1;
        if (op1.position < op2.position) {
            if (op1End < op2.position) {
                return new TextOperation(
                    op1.opName,
                    op1.operand,
                    op1.position,
                    op1.revision
                );
            } else {
                let left = op1.operand.substring(0, op2.position - op1.position);
                let right = op1.operand.substring(left.length);

                // two operations
                return [
                    new TextOperation(op1.opName, left, op1.position, op1.revision),
                    new TextOperation(
                        op1.opName,
                        right,
                        op1.position + left.length + op2.operand.length,
                        op1.revision
                    ),
                ];
            }
        } else {
            return new TextOperation(
                op1.opName,
                op1.operand,
                op1.position + op2.operand.length,
                op1.revision
            );
        }
    }

    // delete-delete
    static transformDD(op1, op2) {
        let op1End = op1.position + op1.operand.length - 1;
        let op2End = op2.position + op2.operand.length - 1;

        if (op1End < op2.position) {
            return new TextOperation(
                op1.opName,
                op1.operand,
                op1.position,
                op1.revision
            );
        } else if (op1.position > op2End) {
            return new TextOperation(
                op1.opName,
                op1.operand,
                op1.position - op2.operand.length,
                op1.revision
            );
        } else if (op1.position < op2.position && op1End >= op2.position) {
            let diff = op2.position - op1.position;
            let operand = op1.operand.substring(0, diff);
            return new TextOperation(op1.opName, operand, op1.position, op1.revision);
        } else if (op1.position <= op2End && op1End > op2End) {
            let diff =
                op1.position + op1.operand.length - (op2.position + op2.operand.length);
            let operand = op1.operand.substring(op1.operand.length - diff);
            return new TextOperation(op1.opName, operand, op2.position, op1.revision);
        } else {
            return null;
        }
    }
}