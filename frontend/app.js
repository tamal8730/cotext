let server = "127.0.0.1:8080"
let httpProtocol = "http"
let wsProtocol = "ws"

let client = null
let docID = null
let prevText = ""
let currText = ""

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

async function onConnect(client) {

    let currUrl = window.location.search
    let url = new URLSearchParams(currUrl)
    let id = url.get("id")
    if (!id) {
        // new document
        let response = await axios.get(`${httpProtocol}://${server}/doc/create`)
        let data = response.data
        docID = data.id

        document.getElementById("docId").textContent = `Collaborate at ${httpProtocol}://127.0.0.1:5500?id=${docID}`

        client.subscribe(`/topic/doc/${docID}`, function (message) {
            let body = message.body;
            let parsed = JSON.parse(body);
            console.log(`Received doc ${body}`)
            if (parsed.opName === "ins") {
                onInsert(parsed.operand, parsed.position)
            } else if (parsed.opName === "del") {
                onDelete(parsed.operand, parsed.position)
            }
            // document.getElementById("editor").value = parsed.content
        })

        console.log(`New doc ${docID}`)

    } else {
        // join document with id=id
        console.log(`joining ${id}`)
        let response = await axios.get(`${httpProtocol}://${server}/doc/${id}`)
        let data = response.data
        docID = data.id
        let content = data.content || ""

        document.getElementById("editor").value = content
        document.getElementById("docId").textContent = `Collaborate at ${httpProtocol}://127.0.0.1:5500?id=${docID}`

        client.subscribe(`/topic/doc/${docID}`, function (message) {
            let body = message.body;
            let parsed = JSON.parse(body);
            console.log(`Received doc ${body}`)
            if (parsed.opName === "ins") {
                onInsert(parsed.operand, parsed.position)
            } else if (parsed.opName === "del") {
                onDelete(parsed.operand, parsed.position)
            }
            // document.getElementById("editor").value = parsed.content
        })

    }


}

function connectOrJoin() {

    let url = `${wsProtocol}://${server}/relay`

    console.log(`Trying to connect to ${url}`)

    client = Stomp.client(url)
    client.connect({}, function () {
        onConnect(client)
    })

}

function onChangeText() {
    let text = document.getElementById("editor").value
    let { start, end } = getCaretPosition(document.getElementById("editor"))
    if (text.length > prevText.length) {
        sendInsertOperation(start, text.substring(start - 1, start))
    } else if (prevText.length > text.length) {
        sendDeleteOperation(start, prevText.substring(start, start + 1))
    }
    prevText = text
}

function sendInsertOperation(caretPosition, substring) {
    client.send(`/app/relay/${docID}`, {}, JSON.stringify({ 'opName': 'ins', 'operand': substring, 'position': caretPosition - 1 }))
}

function sendDeleteOperation(caretPosition, substring) {
    client.send(`/app/relay/${docID}`, {}, JSON.stringify({ 'opName': 'del', 'operand': substring, 'position': caretPosition }))
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

function onInsert(charSequence, position) {
    currText = insertStr(currText, charSequence, position)
    document.getElementById("editor").value = currText
}

function onDelete(charSequence, position) {
    currText = removeCharacter(currText, position)
    document.getElementById("editor").value = currText
}

connectOrJoin()