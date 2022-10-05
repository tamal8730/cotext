package com.github.tamal8730.cotext.shared.operation_queue.impl;

import com.github.tamal8730.cotext.feat_relay_operation.operation_relayer.OperationRelayer;
import com.github.tamal8730.cotext.shared.document_store.DocumentStore;
import com.github.tamal8730.cotext.shared.model.DocumentModel;
import com.github.tamal8730.cotext.shared.model.OperationQueueInPayload;
import com.github.tamal8730.cotext.shared.model.message_out_payload.OperationQueueOutPayload;
import com.github.tamal8730.cotext.shared.operation_queue.OperationQueue;
import org.springframework.beans.factory.annotation.Autowired;

public class OQImpl implements OperationQueue {

    @Autowired
    private DocumentStore documentStore;


    @Autowired
    private OperationRelayer operationRelayer;

    @Override
    public void enqueue(OperationQueueInPayload message) {
        DocumentModel doc = documentStore.getDocumentFromDocId(message.getDocId());

        int serverDocRevision = doc.getRevision();
        int messageDocRevision = message.getRevision();

//        System.out.printf("[PUSH] trying to relay %s\n", message);

        if (messageDocRevision < serverDocRevision) {
            // client doc version is outdated
            // in this case, transform this message against all committed revisions after serverDocVersion
            var transformedOperations = doc.transformAgainstRevisionLogs(message.getOperation(), messageDocRevision);
            if (transformedOperations == null || transformedOperations.isEmpty()) {
                return;
            }

            for (var operation : transformedOperations) {
                if (operation == null) continue;
                operationRelayer.relay(message.getDocId(), new OperationQueueOutPayload(
                        message.getFrom(),
                        operation,
                        doc.getRevision() + 1
                ));
                doc.applyOperation(operation);
            }

        } else if (messageDocRevision == serverDocRevision) {

            operationRelayer.relay(message.getDocId(), new OperationQueueOutPayload(
                    message.getFrom(),
                    message.getOperation(),
                    doc.getRevision() + 1
            ));

            doc.applyOperation(message.getOperation());
        }
    }

}
