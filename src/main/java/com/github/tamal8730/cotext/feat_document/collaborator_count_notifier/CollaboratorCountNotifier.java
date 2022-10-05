package com.github.tamal8730.cotext.feat_document.collaborator_count_notifier;

import com.github.tamal8730.cotext.shared.message_pusher.MessagePusher;
import com.github.tamal8730.cotext.shared.model.message_out_payload.CollaborationCountPayload;
import org.springframework.beans.factory.annotation.Autowired;

public class CollaboratorCountNotifier {

    @Autowired
    public MessagePusher messageRelayer;

    public void notifyCount(String docId, CollaborationCountPayload collaborationCount) {
        messageRelayer.push("collaborator_count", docId, collaborationCount);
    }

}
