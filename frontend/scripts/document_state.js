class DocState {

    constructor(onDocumentChange) {
        this.onDocumentChange = onDocumentChange
        this.sentOperation = null // operation sent to server but not yet acknowledged
        this.pendingOperations = new Deque() // operations not yet sent to server
    }

    lastSyncedRevision = 0
    document = ""
    prevText = ""

    acknowledgeOperation(operation, newRevision, onPendingOperation) {
        // remove sent operation
        this.sentOperation = null
        this.lastSyncedRevision = newRevision

        // take out a pending operation
        if (!this.pendingOperations.isEmpty()) {
            this.sentOperation = this.pendingOperations.dequeueFront()
            onPendingOperation(this.sentOperation)
        }

    }

    setDocumentText(text) {
        this.prevText = this.document
        this.document = text
    }

    async queueOperation(operation, newDocument, onSend) {

        this.setDocumentText(newDocument(this.document))
        console.log(`[DOC] ${this.document}`)

        if (this.sentOperation === null) {
            this.sentOperation = operation
            console.log(`[SEND] sent operation = ${JSON.stringify(operation)}, lastSyncedRevision = ${operation.revision}`)
            await onSend(operation, this.lastSyncedRevision)
        } else {
            console.log(`[ENQ] enqueued operation = ${JSON.stringify(operation)}, lastSyncedRevision = ${this.lastSyncedRevision}`)
            this.pendingOperations.enqueueRear(operation)
        }

    }

    transformPendingOperations(op2, newRevision) {

        if (op2 === null) { return }
        this.pendingOperations.modifyWhere((op1) => OperationTransformation.transformOperation(op1, op2))

    }

    transformOperationAgainstSentOperation(op1) {
        if (this.sentOperation === null) return op1
        let transformed = OperationTransformation.transformOperation(op1, this.sentOperation)
        this.sentOperation = null
        return transformed
    }

    transformOperationAgainstLocalChanges(op1) {
        let transformed = op1
        if (this.sentOperation !== null) {
            transformed = OperationTransformation.transformOperation(transformed, this.sentOperation)
        }
        this.pendingOperations.forEach(op2 => {
            transformed = OperationTransformation.transformOperation(transformed, op2)
        })
        this.sentOperation = null
        return transformed
    }

}

