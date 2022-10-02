package com.github.tamal8730.cotext.feat_enqueue_operation.controller;

import com.github.tamal8730.cotext.feat_enqueue_operation.model.EnqueueOperationPayload;
import com.github.tamal8730.cotext.feat_enqueue_operation.model.EnqueueOperationResponse;
import com.github.tamal8730.cotext.shared.document_store.DocumentStore;
import com.github.tamal8730.cotext.shared.model.DocumentModel;
import com.github.tamal8730.cotext.shared.model.OperationQueueInPayload;
import com.github.tamal8730.cotext.shared.operation_queue.OperationQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin
@RequestMapping("/enqueue")
public class EnqueueOperationController {

    @Autowired
    private DocumentStore documentStore;

    @Autowired
    private OperationQueue operationQueue;

    @PostMapping("/v/{id}")
    private EnqueueOperationResponse enqueue(@PathVariable String id, @RequestBody EnqueueOperationPayload operation) throws Exception {

        System.out.printf("[ENQUEUED], body = %s\n", operation);

        Thread.sleep(500);
        DocumentModel doc = documentStore.getDocument(id);
        if (doc == null) {
            return new EnqueueOperationResponse("error", "document with id = " + id + " does not exist");
        } else {
            operationQueue.enqueue(new OperationQueueInPayload(
                    id,
                    operation.getRevision(),
                    operation.getFrom(),
                    operation.getOperation())
            );
            return new EnqueueOperationResponse("ok", null);
        }
    }

}
