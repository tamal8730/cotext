package com.github.tamal8730.cotext.controller;


import com.github.tamal8730.cotext.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaMessageListener {

    @Autowired
    private DocStateStore docStateStore;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;


    @KafkaListener(topics = "docs", containerFactory = "kafkaListenerContainerFactory")
    public void listener(KafkaMessageModel message) {
        DocState state = docStateStore.getDocState(message.getDocId());

        int serverDocRevision = state.getRevision();
        int messageDocRevision = message.getTextOperationTransient().getRevision();

        System.out.println("LIS " + message.getTextOperationTransient().getOperation() + ", S_V = " + state.getRevision() + ", M_V = " + messageDocRevision);

        if (messageDocRevision < serverDocRevision) {
            // client doc version is outdated
            // in this case, transform this message against all committed revisions after serverDocVersion
            TextOperation transformedOperation = state.applyTransformationsFrom(message.getTextOperationTransient().getOperation(), messageDocRevision);
            if (transformedOperation == null) return;
            simpMessagingTemplate.convertAndSend("/topic/doc/" + message.getDocId()
                    , new TextOperationResponse(
                            message.getTextOperationTransient().getFrom(),
                            transformedOperation,
                            state.getRevision() + 1
                    )
            );
            state.removeCurrentOperationAndApplyTransformedOperation(transformedOperation);
        } else if (messageDocRevision == serverDocRevision) {
            simpMessagingTemplate.convertAndSend("/topic/doc/" + message.getDocId()
                    , new TextOperationResponse(
                            message.getTextOperationTransient().getFrom(),
                            message.getTextOperationTransient().getOperation(),
                            state.getRevision() + 1
                    )
            );
            state.applyCurrentOperation(message.getTextOperationTransient().getOperation());
        }
    }

}
