package com.github.tamal8730.cotext.controller;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.github.tamal8730.cotext.model.DocStore;
import com.github.tamal8730.cotext.model.DocumentModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin
@RequestMapping("/doc")
public class RelayController {

    @Autowired
    private DocStore docStore;

    @GetMapping("/create")
    public DocumentModel createDoc() {

        String id = NanoIdUtils.randomNanoId();
        return docStore.addDoc(id);

    }

    @GetMapping("/{id}")
    public DocumentModel join(@PathVariable String id) {
        return docStore.getDoc(id);
    }

}
