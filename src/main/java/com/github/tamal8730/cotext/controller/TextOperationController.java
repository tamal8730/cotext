package com.github.tamal8730.cotext.controller;

import com.github.tamal8730.cotext.model.DocStore;
import com.github.tamal8730.cotext.model.TextOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@Controller
@CrossOrigin
public class TextOperationController {

    @Autowired
    private DocStore docStore;

    @MessageMapping("/relay/{id}")
    @SendTo("/topic/doc/{id}")
    private TextOperation relayOperation(@DestinationVariable String id, TextOperation operation) throws Exception {
        Thread.sleep(500);
        return operation;
    }


}
