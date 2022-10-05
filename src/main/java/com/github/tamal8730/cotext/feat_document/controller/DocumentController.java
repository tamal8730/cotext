package com.github.tamal8730.cotext.feat_document.controller;


import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.github.tamal8730.cotext.feat_document.collaborator_count_notifier.CollaboratorCountNotifier;
import com.github.tamal8730.cotext.feat_document.model.DocumentCreateResponse;
import com.github.tamal8730.cotext.feat_document.model.DocumentJoinResponse;
import com.github.tamal8730.cotext.shared.document_store.DocumentStore;
import com.github.tamal8730.cotext.shared.model.DocumentModel;
import com.github.tamal8730.cotext.shared.model.message_out_payload.CollaborationCountPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/doc")
@CrossOrigin
public class DocumentController {

    @Autowired
    private DocumentStore documentStore;

    @Autowired
    private CollaboratorCountNotifier collaboratorCountRelayer;

    @GetMapping("/create")
    public DocumentCreateResponse createDoc() {

        String docId = NanoIdUtils.randomNanoId();
        DocumentModel doc = documentStore.addEmptyDocument(docId);
        String newUserId = UUID.randomUUID().toString();
        return new DocumentCreateResponse(newUserId, docId);

    }

    @GetMapping("/{id}")
    public DocumentJoinResponse joinDoc(@PathVariable String id) {

        DocumentModel doc = documentStore.getDocument(id);
        if (doc == null) {
            return DocumentJoinResponse.withError("document with id = " + id + " does not exist");
        } else {

            doc.addCollaborator();
            collaboratorCountRelayer.notifyCount(id, new CollaborationCountPayload(doc.getCollaboratorCount()));

            String newUserId = UUID.randomUUID().toString();
            return DocumentJoinResponse.noError(doc.getCollaboratorCount(), newUserId, doc.getDocText(), doc.getRevision());
        }

    }

}