let serverAddress = "127.0.0.1"
let serverPort = "8080"
let hostPort = "5500"
let httpProtocol = "http"
let wsProtocol = "ws"

let client = null
let docId = null
let userId = null

document.getElementById("editor").oninput = onChangeText
document.getElementById("editor").onpaste = onPaste


class DequeNode {
    constructor(val) {
        this.val = val
        this.next = null
        this.prev = null
    }
}

class Deque {

    front = null
    rear = null

    enqueueFront(element) {
        let newNode = new DequeNode(element)

        if (this.front === null && this.rear === null) {
            this.front = newNode
            this.rear = newNode
        } else {
            newNode.prev = this.front
            this.front.next = newNode
            this.front = newNode
        }
    }

    enqueueRear(element) {
        let newNode = new DequeNode(element)

        if (this.front === null && this.rear === null) {
            this.front = newNode
            this.rear = newNode
        } else {
            this.rear.prev = newNode
            newNode.next = this.rear
            this.rear = newNode
        }
    }

    dequeueFront() {

        if (this.front === null) {
            return null
        }

        let toRemove = this.front
        let prev = toRemove.prev
        toRemove.prev = null
        if (prev === null) {

            this.front = null
            this.rear = null

        } else {
            prev.next = null
            this.front = prev
        }

        return toRemove.val

    }

    modifyWhere(replaceWith) {
        let ptr = this.front
        while (ptr !== null && ptr !== undefined) {

            let replacement = replaceWith(ptr.val)

            if (replacement !== null && replacement !== undefined) {

                if (replacement.constructor === Array) {

                    let subqueue = new Deque()
                    for (let i = 0; i < replacement.length; i++) {
                        subqueue.enqueueRear(replacement[i])
                    }

                    let toRemove = ptr
                    let prev = ptr.prev
                    let next = ptr.next

                    toRemove.prev = null
                    toRemove.next = null

                    subqueue.rear.prev = prev
                    subqueue.front.next = next


                    if (prev === null) {
                        this.rear = subqueue.rear
                    } else {
                        prev.next = subqueue.rear
                    }

                    if (next === null) {
                        this.front = subqueue.front
                    } else {
                        next.prev = subqueue.front
                    }

                    ptr = subqueue.rear

                    delete toRemove.val
                    delete toRemove.prev
                    delete toRemove.next


                } else {
                    ptr.val = replacement
                }

            }

            ptr = ptr.prev
        }
    }

    isEmpty() {
        return this.front === null && this.rear === null
    }

    forEach(callback) {
        let ptr = this.front
        while (ptr !== null) {
            callback(ptr.val)
            ptr = ptr.prev
        }
    }

}

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
            await onSend(operation, docState.lastSyncedRevision)
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



let docState = new DocState((newDoc) => {
    document.getElementById("editor").value = newDoc
})





function getCaretPosition(textarea) {

    if (document.selection) {
        textarea.focus()
        var range = document.selection.createRange()
        var rangeLen = range.text.length
        range.moveStart('character', -textarea.value.length)
        var start = range.text.length - rangeLen
        return {
            'start': start,
            'end': start + rangeLen
        }
    }
    else if (textarea.selectionStart || textarea.selectionStart == '0') {
        return {
            'start': textarea.selectionStart,
            'end': textarea.selectionEnd
        }
    } else {
        return {
            'start': 0,
            'end': 0
        }
    }

}


function onOperationAcknowledged(operation, revision) {
    if (docState.lastSyncedRevision < revision) {

        console.log(`[ACK] acknowledged operation = ${JSON.stringify(operation)}, revision = ${revision}`)

        docState.acknowledgeOperation(
            operation,
            revision,
            (pendingOperation) => {
                console.log(`[DEQ] sending operation = ${JSON.stringify(pendingOperation)}, revision = ${revision}`)
                sendOperation(pendingOperation, docState.lastSyncedRevision)
            }
        )

    }
}


function subscribeToDocumentUpdates(docId) {

    client.subscribe(`/topic/doc/${docId}`, function (message) {

        let body = message.body;
        let parsed = JSON.parse(body);

        let ack = parsed.acknowledgeTo
        let operation = parsed.operation
        let revision = parsed.revision

        if (ack === userId) {

            onOperationAcknowledged(operation, revision)

        } else {

            docState.transformPendingOperations(operation, revision)
            docState.lastSyncedRevision = revision

            // transformedOperation = docState.transformOperationAgainstSentOperation(operation)
            transformedOperation = docState.transformOperationAgainstLocalChanges(operation)

            if (transformedOperation === null) return

            console.log(`[APPLY] applied operation = ${JSON.stringify(transformedOperation)}, revision = ${revision}`)

            if (transformedOperation.opName === "ins") {
                onInsert(transformedOperation.operand, transformedOperation.position, revision)
            } else if (transformedOperation.opName === "del") {
                onDelete(transformedOperation.operand, transformedOperation.position, revision)
            }

        }

    })

}

async function onNewDocument() {

    let response = await axios.get(`${httpProtocol}://${serverAddress}:${serverPort}/doc/create`)
    let data = response.data
    docId = data.docId
    userId = data.userId

    document.getElementById("shareable_link").textContent = `${httpProtocol}://${serverAddress}:${hostPort}?id=${docId}`
    subscribeToDocumentUpdates(docId)

}

async function onDocumentJoin(id) {

    let response = await axios.get(`${httpProtocol}://${serverAddress}:${serverPort}/doc/${id}`)
    let data = response.data
    let hasError = data.hasError

    if (hasError) { throw 'No such document' }

    docId = id
    userId = data.userId
    docState.lastSyncedRevision = data.documentRevision
    docState.setDocumentText(data.text || "")

    document.getElementById("editor").value = docState.document
    document.getElementById("shareable_link").textContent = `${httpProtocol}://${serverAddress}:${hostPort}?id=${docId}`

    subscribeToDocumentUpdates(docId)

}

async function onConnect(client) {

    let currUrl = window.location.search
    let url = new URLSearchParams(currUrl)
    let id = url.get("id")
    if (!id) {
        await onNewDocument() // new document
    } else {
        await onDocumentJoin(id) // join document with id=id
    }

}

function connectOrJoin() {

    let url = `${wsProtocol}://${serverAddress}:${serverPort}/relay`

    client = Stomp.client(url)
    client.connect({}, function (frame) {
        onConnect(client)
    })

}

function onPaste(param) {

    let editor = document.getElementById("editor")
    let { start, end } = getCaretPosition(editor)
    let pastedText = param.clipboardData.getData("text")

    // delete selection
    if (start !== end) {
        let substr = editor.value.substring(start, end)
        console.log(`[DEL] '${substr}' at ${start}`)
        sendDeleteOperation(start, substr)
    }

    // insert pasted text
    console.log(`[INS] '${pastedText}' at ${start}`)
    sendInsertOperation(start + 1, pastedText)

}

function onChangeText(event) {

    let inputType = event.inputType

    let editor = document.getElementById("editor")
    let currText = editor.value
    let prevText = docState.document
    let { start, end } = getCaretPosition(editor)

    console.log(`[CHANGE] ${inputType}`)

    if (inputType === "insertText") {

        // delete the selected text
        if (currText.length <= prevText.length) {
            let charsToDeleteAfterStart = prevText.length - currText.length
            let substr = prevText.substring(start - 1, start + charsToDeleteAfterStart)
            console.log(`[DEL] '${substr}' at ${start - 1}`)
            sendDeleteOperation(start - 1, substr)
        }

        // insert the typed character
        console.log(`[INS] '${currText.substring(start - 1, start)}' at ${start}`)
        sendInsertOperation(start, currText.substring(start - 1, start))

    } else if (inputType === "insertLineBreak") {

        sendInsertOperation(start, currText.substring(start - 1, start))

    } else if (inputType === "deleteByCut" || inputType === "deleteContentBackward" || inputType === "deleteContentForward") {

        let charactersDeleted = prevText.length - currText.length
        let deletedString = prevText.substring(start, start + charactersDeleted)
        sendDeleteOperation(start, deletedString)

    } else {
        // unsupported operation
    }

}

function createOperationPayload(operation, revision) {
    return {
        'operation': { 'opName': operation.opName, 'operand': operation.operand, 'position': operation.position },
        'revision': revision, 'from': userId
    }
}


async function sendOperation(operation, revision) {

    if (operation.opName === "ins" || operation.opName === "del") {

        let body = createOperationPayload(operation, revision)

        console.log(`[POST] ${JSON.stringify(body)}`)

        await axios.post(`${httpProtocol}://${serverAddress}:${serverPort}/enqueue/${docId}`, body)

    } else {
        // unsupported operation
    }

}


function sendInsertOperation(caretPosition, substring) {

    docState.queueOperation(

        new TextOperation("ins", substring, caretPosition - 1, docState.lastSyncedRevision),

        (currDoc) => insertSubstring(currDoc, substring, caretPosition - 1),

        async (operation, revision) => { await sendOperation(operation, revision) }

    )

}


function sendDeleteOperation(caretPosition, substring) {

    docState.queueOperation(

        new TextOperation("del", substring, caretPosition, docState.lastSyncedRevision),

        (currDoc) => removeSubstring(currDoc, caretPosition, caretPosition + substring.length),

        async (operation, revision) => { await sendOperation(operation, revision) }

    )

}


function insertSubstring(mainString, substring, pos) {
    if (typeof (pos) == "undefined") {
        pos = 0;
    }
    if (typeof (substring) == "undefined") {
        substring = "";
    }
    return mainString.slice(0, pos) + substring + mainString.slice(pos);
}

function removeSubstring(str, start, end) {
    return str.substring(0, start) + str.substring(end);
}

function onInsert(charSequence, position, revision) {
    docState.setDocumentText(insertSubstring(docState.document, charSequence, position))
    document.getElementById("editor").value = docState.document
}

function onDelete(charSequence, position, revision) {
    docState.setDocumentText(removeSubstring(docState.document, position, charSequence.length + position))
    document.getElementById("editor").value = docState.document
}

connectOrJoin()