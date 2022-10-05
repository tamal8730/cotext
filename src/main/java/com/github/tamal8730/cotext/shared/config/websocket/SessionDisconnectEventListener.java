package com.github.tamal8730.cotext.shared.config.websocket;

import com.github.tamal8730.cotext.feat_document.collaborator_count_notifier.CollaboratorCountNotifier;
import com.github.tamal8730.cotext.shared.document_store.DocumentStore;
import com.github.tamal8730.cotext.shared.model.message_out_payload.CollaborationCountPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class SessionDisconnectEventListener implements ApplicationListener<SessionDisconnectEvent> {

    @Autowired
    public DocumentStore documentStore;

    @Autowired
    public CollaboratorCountNotifier collaboratorCountNotifier;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        String userId = event.getUser().getName();
        var doc = documentStore.removeCollaboratorFromDocument(userId);
        if (doc.getCollaboratorCount() > 0) {
            collaboratorCountNotifier.notifyCount(doc.getId(), new CollaborationCountPayload(doc.getCollaboratorCount()));
        }
    }

}
