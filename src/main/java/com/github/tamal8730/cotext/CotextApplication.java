package com.github.tamal8730.cotext;

import com.github.tamal8730.cotext.feat_document.collaborator_count_notifier.CollaboratorCountNotifier;
import com.github.tamal8730.cotext.feat_document.formatter.impl.CharSequenceDocumentFormatter;
import com.github.tamal8730.cotext.shared.config.websocket.HttpHandshakeInterceptor;
import com.github.tamal8730.cotext.feat_relay_operation.operation_relayer.OperationRelayer;
import com.github.tamal8730.cotext.shared.document_store.DocumentStore;
import com.github.tamal8730.cotext.shared.document_store.impl.SimpleHashMapDocumentStore;
import com.github.tamal8730.cotext.shared.operation_queue.OperationQueue;
import com.github.tamal8730.cotext.shared.operation_queue.impl.OQImpl;
import com.github.tamal8730.cotext.shared.operation_transformations.OperationTransformations;
import com.github.tamal8730.cotext.shared.operation_transformations.impl.CharSequenceOperationTransformations;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CotextApplication {

    @Bean
    public HttpHandshakeInterceptor getHttpHandshakeInterceptor() {
        return new HttpHandshakeInterceptor();
    }

    @Bean
    public OperationQueue getOperationQueue() {
        return new OQImpl();
    }

    @Bean
    public OperationRelayer getOperationRelayer() {
        return new OperationRelayer();
    }

    @Bean
    public OperationTransformations getOperationTransformations() {
        return new CharSequenceOperationTransformations();
    }

    @Bean
    public DocumentStore getDocumentStore() {
        return new SimpleHashMapDocumentStore(CharSequenceDocumentFormatter::new);
    }

    @Bean
    public CollaboratorCountNotifier getCollaboratorCountNotifier() {
        return new CollaboratorCountNotifier();
    }

    public static void main(String[] args) {
        SpringApplication.run(CotextApplication.class, args);
    }

}
