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