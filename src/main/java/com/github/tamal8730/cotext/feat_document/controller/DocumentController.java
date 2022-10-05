package com.github.tamal8730.cotext.feat_document.controller;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.github.tamal8730.cotext.feat_document.collaborator_count_notifier.CollaboratorCountNotifier;
import com.github.tamal8730.cotext.feat_document.model.DocumentCreateResponse;
import com.github.tamal8730.cotext.feat_document.model.DocumentJoinResponse;
import com.github.tamal8730.cotext.feat_document.model.UserModel;
import com.github.tamal8730.cotext.shared.document_store.DocumentStore;
import com.github.tamal8730.cotext.shared.model.DocumentModel;
import com.github.tamal8730.cotext.shared.model.message_out_payload.CollaborationCountPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/doc")
@CrossOrigin
public class DocumentController {

    @Autowired
    private DocumentStore documentStore;

    @Autowired
    private CollaboratorCountNotifier collaboratorCountRelayer;

    @PostMapping("/create")
    public DocumentCreateResponse createDoc(@RequestBody UserModel user) {

        String docId = NanoIdUtils.randomNanoId();
        documentStore.addEmptyDocument(user.getUserId(), docId);
        return new DocumentCreateResponse(docId);

    }

    @PostMapping("/{id}")
    public DocumentJoinResponse joinDoc(@PathVariable String id, @RequestBody UserModel user) {

        DocumentModel doc = documentStore.getDocumentFromDocId(id);
        if (doc == null) {
            return DocumentJoinResponse.withError("document with id = " + id + " does not exist");
        } else {

            documentStore.addCollaboratorToDocument(user.getUserId(), id);
            collaboratorCountRelayer.notifyCount(id, new CollaborationCountPayload(doc.getCollaboratorCount()));

            return DocumentJoinResponse.noError(doc.getCollaboratorCount(), doc.getDocText(), doc.getRevision());
        }

    }

}