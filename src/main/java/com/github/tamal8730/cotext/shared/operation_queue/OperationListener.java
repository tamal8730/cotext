package com.github.tamal8730.cotext.shared.operation_queue;

import com.github.tamal8730.cotext.feat_relay_operation.operation_relayer.OperationRelayer;
import com.github.tamal8730.cotext.shared.document_store.DocumentStore;
import com.github.tamal8730.cotext.shared.model.DocumentModel;
import com.github.tamal8730.cotext.shared.model.OperationQueueInPayload;
import com.github.tamal8730.cotext.shared.model.OperationQueueOutPayload;
import com.github.tamal8730.cotext.shared.model.TextOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class OperationListener {

    @Autowired
    private DocumentStore documentStore;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private OperationRelayer operationRelayer;

    @KafkaListener(topics = "docs", containerFactory = "kafkaListenerContainerFactory")
    public void listener(OperationQueueInPayload message) {
        DocumentModel doc = documentStore.getDocument(message.getDocId());

        int serverDocRevision = doc.getRevision();
        int messageDocRevision = message.getRevision();

        if (messageDocRevision < serverDocRevision) {
            // client doc version is outdated
            // in this case, transform this message against all committed revisions after serverDocVersion
            TextOperation transformedOperation = doc.applyTransformationsAgainstRevisionLogsFrom(message.getOperation(), messageDocRevision);
            if (transformedOperation == null) {
                return;
            }
            operationRelayer.relay(message.getDocId(), new OperationQueueOutPayload(
                    message.getFrom(),
                    transformedOperation,
                    doc.getRevision() + 1
            ));
            doc.applyOperation(transformedOperation);
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
