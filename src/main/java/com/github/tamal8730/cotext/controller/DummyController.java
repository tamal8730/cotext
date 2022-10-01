package com.github.tamal8730.cotext.controller;


import com.github.tamal8730.cotext.model.DocState;
import com.github.tamal8730.cotext.model.DocStateStore;
import com.github.tamal8730.cotext.model.KafkaMessageModel;
import com.github.tamal8730.cotext.model.TextOperationTransient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/send")
@CrossOrigin
public class DummyController {

    @Autowired
    private DocStateStore docStateStore;

    @Autowired
    private KafkaTemplate<String, KafkaMessageModel> kafkaTemplate;

    @PostMapping("/message/{id}")
    private String send(@PathVariable String id, @RequestBody TextOperationTransient operation) throws Exception {
        Thread.sleep(5000);
        DocState state = docStateStore.getDocState(id);
        kafkaTemplate.send("docs", new KafkaMessageModel(id, operation));
        return "OK";
    }

}
