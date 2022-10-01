package com.github.tamal8730.cotext.controller;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.github.tamal8730.cotext.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin
@RequestMapping("/doc")
public class DocController {

    @Autowired
    private DocStateStore stateStore;

    @GetMapping("/create")
    public DocumentCreateResponse createDoc() {

        String docId = NanoIdUtils.randomNanoId();
        DocState docState = stateStore.addEmptyDoc(docId);
        return new DocumentCreateResponse(docId);

    }

    @GetMapping("/{id}")
    public DocumentJoinResponse join(@PathVariable String id) {
        DocState docState = stateStore.getDocState(id);
        // TODO: handle doc-not-found case
        return new DocumentJoinResponse(docState.getDocText());
    }

}
