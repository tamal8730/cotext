package com.github.tamal8730.cotext.controller;


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
    @Qualifier("kafkaTemplate")
    private KafkaTemplate<String, KafkaMessageModel> kafkaTemplate;

    @PostMapping("/message/{id}")
    private String send(@PathVariable String id, @RequestBody TextOperationTransient operation) throws Exception {
        kafkaTemplate.send("docs", new KafkaMessageModel(id, operation));
        return "OK";
    }

}
