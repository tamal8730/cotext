package com.github.tamal8730.cotext.controller;

import com.github.tamal8730.cotext.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class TextOperationController {

    @Autowired
    private DocStateStore docStateStore;


    @MessageMapping("/relay/{id}")
    @SendTo("/topic/doc/{id}")
    private TextOperationResponse relayOperation(@DestinationVariable String id, TextOperationTransient operation) throws Exception {
        DocState state = docStateStore.getDocState(id);
        state.addPendingOperation(operation);
        // TODO: operate
        state.applyCurrentOperation();
        return new TextOperationResponse(operation.getFrom(), operation.getOperation(), state.getRevision());
    }

}
