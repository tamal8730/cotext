let server = "127.0.0.1:8080"
let httpProtocol = "http"
let wsProtocol = "ws"

let client = null
let docId = null
let userId = null



class Queue {
    constructor() {
        this.elements = {};
        this.head = 0;
        this.tail = 0;
    }
    enqueue(element) {
        this.elements[this.tail] = element;
        this.tail++;
    }
    dequeue() {
        const item = this.elements[this.head];
        delete this.elements[this.head];
        this.head++;
        return item;
    }
    peek() {
        return this.elements[this.head];
    }
    get length() {
        return this.tail - this.head;
    }
    get isEmpty() {
        return this.length === 0;
    }

    replaceWhere(where) {
        for (let i = this.tail - 1; i >= this.head; i--) {
            let newElement = where(this.elements[i])
            if (newElement !== null) {
                this.elements[i] = newElement
            }
        }
    }

}


class TextOperation {

    constructor(opName, operand, position, revision) {
        this.opName = opName
        this.operand = operand
        this.position = position
        this.revision = revision
    }

}



class DocState {

    constructor(onDocumentChange) {
        this.onDocumentChange = onDocumentChange
        this.sentOperation = null // operation sent to server but not yet acknowledged
        this.pendingOperations = new Queue() // operations not yet sent to server
    }

    lastSyncedRevision = 0
    document = ""
    prevText = ""

    acknowledgeOperation(operation, newRevision, onPendingOperation) {
        // remove sent operation
        this.sentOperation = null
        this.lastSyncedRevision = newRevision

        // take out a pending operation
        if (!this.pendingOperations.isEmpty) {
            this.sentOperation = this.pendingOperations.dequeue()
            onPendingOperation(this.sentOperation)
        }

    }

    setDocumentText(text) {
        this.prevText = this.document
        this.document = text
    }

    queueOperation(operation, newDocument, onSend) {

        this.setDocumentText(newDocument(this.document))

        if (this.sentOperation === null) {
            console.log(`[SEND] sent operation = ${JSON.stringify(operation)}, lastSyncedRevision = ${operation.revision}`)
            onSend(operation.revision)
            this.sentOperation = operation
        } else {
            console.log(`[ENQ] enqueued operation = ${JSON.stringify(operation)}, lastSyncedRevision = ${this.lastSyncedRevision}`)
            this.pendingOperations.enqueue(operation)
        }

    }

    transformPendingOperations(op2, newRevision) {

        if (op2 === null) { return }
        this.pendingOperations.replaceWhere((op1) => this.transformOperation(op1, op2))

    }

    transformOperationAgainstSentOperation(op1) {
        if (this.sentOperation === null) return op1
        let transformed = this.transformOperation(op1, this.sentOperation)
        this.sentOperation = null
        return transformed
    }

    transformOperation(op1, op2) {

        let op1Name = op1.opName
        let op2Name = op2.opName

        if (op1Name == "ins" && op2Name == "ins") return this.transformII(op1, op2)
        else if (op1Name == "ins" && op2Name == "del") return this.transformID(op1, op2)
        else if (op1Name == "del" && op2Name == "ins") return this.transformDI(op1, op2)
        else if (op1Name == "del" && op2Name == "del") return this.transformDD(op1, op2)
        else return null

    }

    // insert-insert transform
    transformII(op1, op2) {
        if (op1.position < op2.position) return new TextOperation(op1.opName, op1.operand, op1.position)
        else return new TextOperation(op1.opName, op1.operand, op1.position + 1, op1.revision)
    }


    // insert-delete
    transformID(op1, op2) {
        let newPos = 0
        if (op1.position <= op2.position) newPos = op1.position
        else newPos = op1.position - 1
        return new TextOperation(op1.opName, op1.operand, newPos, op1.revision)
    }

    // delete-insert
    transformDI(op1, op2) {
        let newPos = 0
        if (op1.position < op2.position) newPos = op1.position
        else newPos = op1.position + 1
        return new TextOperation(op1.opName, op1.operand, newPos, op1.revision)
    }

    // delete-delete
    transformDD(op1, op2) {
        let newPos = 0
        if (op1.position < op2.position) newPos = op1.position
        else if (op1.position > op2.position) newPos = op1.position - 1
        else return null
        return new TextOperation(op1.opName, op1.operand, newPos, op1.revision)
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
                sendOperation(pendingOperation)
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

            transformedOperation = docState.transformOperationAgainstSentOperation(operation)
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

    let response = await axios.get(`${httpProtocol}://${server}/doc/create`)
    let data = response.data
    docId = data.docId
    userId = data.userId

    document.getElementById("docId").textContent = `Collaborate at ${httpProtocol}://127.0.0.1:5500?id=${docId}`
    subscribeToDocumentUpdates(docId)

}

async function onDocumentJoin(id) {

    let response = await axios.get(`${httpProtocol}://${server}/doc/${id}`)
    let data = response.data
    docId = id
    userId = data.userId
    docState.setDocumentText(data.text || "")

    document.getElementById("editor").value = docState.document
    document.getElementById("docId").textContent = `Collaborate at ${httpProtocol}://127.0.0.1:5500?id=${docId}`

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

    let url = `${wsProtocol}://${server}/relay`

    client = Stomp.client(url)
    client.connect({}, function () { onConnect(client) })

}

function onChangeText() {

    let currText = document.getElementById("editor").value
    let prevText = docState.document
    let { start, end } = getCaretPosition(document.getElementById("editor"))

    if (currText.length > prevText.length) {
        sendInsertOperation(start, currText.substring(start - 1, start))
    } else if (prevText.length > currText.length) {
        sendDeleteOperation(start, prevText.substring(start, start + 1))
    }

}

function sendOperation(operation) {

    if (operation.opName === "ins") {

        axios.post(`${httpProtocol}://${server}/send/message/${docId}`,
            {
                'operation': { 'opName': operation.opName, 'operand': operation.operand, 'position': operation.position },
                'revision': docState.lastSyncedRevision, 'from': userId
            }
        )

        // docState.queueOperation(
        //     operation,
        //     (currDoc) => currDoc,
        //     async (revision) => {
        //         await axios.post(`${httpProtocol}://${server}/send/message/${docId}`,
        //             {
        //                 'operation': { 'opName': operation.opName, 'operand': operation.operand, 'position': operation.position },
        //                 'revision': revision, 'from': userId
        //             }
        //         )
        //     }
        //     // (lastSyncedRevision) => { client.send(`/app/relay/${docId}`, {}, JSON.stringify({ 'operation': { 'opName': operation.opName, 'operand': operation.operand, 'position': operation.position }, 'revision': lastSyncedRevision, 'from': userId })) }
        // )

    } else if (operation.opName === "del") {


        axios.post(`${httpProtocol}://${server}/send/message/${docId}`,
            {
                'operation': {
                    'opName': operation.opName,
                    'operand': operation.operand,
                    'position': operation.position
                },
                'revision': docState.lastSyncedRevision, 'from': userId
            }
        )

        // docState.queueOperation(
        //     operation,
        //     (currDoc) => currDoc,
        //     async (revision) => {
        //         await axios.post(`${httpProtocol}://${server}/send/message/${docId}`,
        //             {
        //                 'operation': {
        //                     'opName': operation.opName,
        //                     'operand': operation.operand,
        //                     'position': operation.position
        //                 },
        //                 'revision': revision, 'from': userId
        //             }
        //         )
        //     }
        //     // (lastSyncedRevision) => { client.send(`/app/relay/${docId}`, {}, JSON.stringify({ 'operation': { 'opName': operation.opName, 'operand': operation.operand, 'position': operation.position }, 'revision': lastSyncedRevision, 'from': userId })) }
        // )

    } else {

    }

}

function sendInsertOperation(caretPosition, substring) {

    docState.queueOperation(
        new TextOperation("ins", substring, caretPosition - 1, docState.lastSyncedRevision),
        (currDoc) => insertStr(currDoc, substring, caretPosition - 1),
        async (revision) => {
            await axios.post(
                `${httpProtocol}://${server}/send/message/${docId}`,
                {
                    'operation': { 'opName': 'ins', 'operand': substring, 'position': caretPosition - 1 },
                    'revision': revision, 'from': userId
                }
            )
        }
        // (lastSyncedRevision) => { client.send(`/app/relay/${docId}`, {}, JSON.stringify({ 'operation': { 'opName': 'ins', 'operand': substring, 'position': caretPosition - 1 }, 'revision': lastSyncedRevision, 'from': userId })) }
    )

}

function sendDeleteOperation(caretPosition, substring) {

    docState.queueOperation(
        new TextOperation("del", substring, caretPosition, docState.lastSyncedRevision),
        (currDoc) => removeCharacter(currDoc, caretPosition),
        async (revision) => {
            await axios.post(`${httpProtocol}://${server}/send/message/${docId}`,
                {
                    'operation': { 'opName': 'del', 'operand': substring, 'position': caretPosition },
                    'revision': revision, 'from': userId
                }
            )
        }
        // (lastSyncedRevision) => { client.send(`/app/relay/${docId}`, {}, JSON.stringify({ 'operation': { 'opName': 'ins', 'operand': substring, 'position': caretPosition }, 'revision': lastSyncedRevision, 'from': userId })) }
    )

}

function insertStr(main_string, ins_string, pos) {
    if (typeof (pos) == "undefined") {
        pos = 0;
    }
    if (typeof (ins_string) == "undefined") {
        ins_string = '';
    }
    return main_string.slice(0, pos) + ins_string + main_string.slice(pos);
}

function removeCharacter(str, char_pos) {
    part1 = str.substring(0, char_pos);
    part2 = str.substring(char_pos + 1, str.length);
    return (part1 + part2);
}

function onInsert(charSequence, position, revision) {
    docState.setDocumentText(insertStr(docState.document, charSequence, position))
    document.getElementById("editor").value = docState.document
}

function onDelete(charSequence, position, revision) {
    docState.setDocumentText(removeCharacter(docState.document, position))
    document.getElementById("editor").value = docState.document
}

connectOrJoin()