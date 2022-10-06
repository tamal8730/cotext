(function () {

    let origin = window.location.origin
    let wsprotocol = window.location.protocol === "https" ? "wss" : "ws"
    let host = window.location.host

    let docId = null
    let userId = null

    document.getElementById("editor").oninput = onChangeText
    document.getElementById("editor").onpaste = onPaste


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


    function handleOperation(payload) {

        let ack = payload.acknowledgeTo
        let operation = payload.operation
        let revision = payload.revision

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
    }


    function handleCollaboratorCount(payload) {
        let count = payload.count;
        setCollaboratorCount(count)
    }

    function setCollaboratorCount(count) {
        let collaboratorCount = count - 1
        let text = ""
        if (collaboratorCount === 1) {
            text = "You +1 collaborator";
        } else if (collaboratorCount > 1) {
            text = `You + ${collaboratorCount} collaborators`;
        }
        document.getElementById("collaborator_count").innerText = text
    }


    function subscribeToDocumentUpdates(client, docId) {

        client.subscribe(`/topic/doc/${docId}`, function (message) {

            let body = message.body;
            let parsed = JSON.parse(body);

            let type = parsed.type;
            let payload = parsed.payload;

            switch (type) {
                case "operation":
                    handleOperation(payload)
                    break;
                case "collaborator_count":
                    handleCollaboratorCount(payload)
                    break
            }

        })

    }

    async function onNewDocument(client) {
        let response = await axios.post(`${origin}/doc/create`, {
            'userId': userId
        })
        // let response = await axios.get(`${httpProtocol}://${serverAddress}:${serverPort}/doc/create`)
        let data = response.data
        docId = data.docId
        // userId = data.userId

        document.getElementById("shareable_link").textContent = `${origin}?id=${docId}`
        subscribeToDocumentUpdates(client, docId)
    }

    async function onDocumentJoin(client, id) {

        let response = await axios.post(`${origin}/doc/${id}`, {
            'userId': userId
        })
        let data = response.data
        let hasError = data.hasError

        if (hasError) { throw 'No such document' }

        docId = id
        // userId = data.userId
        docState.lastSyncedRevision = data.documentRevision
        docState.setDocumentText(data.text || "")

        document.getElementById("editor").value = docState.document
        document.getElementById("shareable_link").textContent = `${origin}?id=${docId}`
        setCollaboratorCount(data.collaboratorCount)

        subscribeToDocumentUpdates(client, docId)

    }

    async function onConnect(client, id) {

        if (!id) {
            await onNewDocument(client) // new document
        } else {
            await onDocumentJoin(client, id) // join document with docId=id
        }

    }

    function connectOrJoin() {

        let currUrl = window.location.search
        let urlParams = new URLSearchParams(currUrl)
        let id = urlParams.get("id")

        let url = `${wsprotocol}://${host}/relay${id ? `?id=${id}` : ""}`


        let client = Stomp.client(url)
        client.connect(

            {},

            (frame) => {
                let headers = frame.headers
                console.log(headers)
                let userName = headers["user-name"]
                userId = userName
                onConnect(client, id)
            },

            (err) => {
                document.getElementById("hero").innerHTML = `<p>404<br>The requested page was not found<br>Also, sorry for this awful error page :(</p>`
            },

        )


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

            await axios.post(`${origin}/enqueue/${docId}`, body)

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

        let editor = document.getElementById("editor")
        editor.value = docState.document

        editor.style.height = "auto";
        let scrollHeight = editor.scrollHeight;
        editor.style.height = `${scrollHeight}px`;

    }

    function onDelete(charSequence, position, revision) {
        docState.setDocumentText(removeSubstring(docState.document, position, charSequence.length + position))

        let editor = document.getElementById("editor")
        editor.value = docState.document

        editor.style.height = "auto";
        let scrollHeight = editor.scrollHeight;
        editor.style.height = `${scrollHeight}px`;
    }

    try {
        connectOrJoin()
    } catch (e) {
        document.getElementById("hero").innerHTML = `<p>error loading page</p>`
    }


}).call(this)