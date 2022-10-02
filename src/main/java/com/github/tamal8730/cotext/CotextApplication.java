package com.github.tamal8730.cotext;

import com.github.tamal8730.cotext.shared.config.websocket.HttpHandshakeInterceptor;
import com.github.tamal8730.cotext.feat_document.formatter.impl.DocumentFormatterImpl;
import com.github.tamal8730.cotext.feat_relay_operation.operation_relayer.OperationRelayer;
import com.github.tamal8730.cotext.feat_relay_operation.operation_relayer.impl.StompOperationRelayer;
import com.github.tamal8730.cotext.shared.document_store.DocumentStore;
import com.github.tamal8730.cotext.shared.document_store.impl.SimpleHashMapDocumentStore;
import com.github.tamal8730.cotext.shared.operation_queue.OperationQueue;
import com.github.tamal8730.cotext.shared.operation_queue.impl.KafkaOperationQueue;
import com.github.tamal8730.cotext.shared.operation_transformations.OperationTransformations;
import com.github.tamal8730.cotext.shared.operation_transformations.impl.SimpleCharacterOperationTransformations;
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
        return new KafkaOperationQueue();
    }

    @Bean
    public OperationRelayer getOperationRelayer() {
        return new StompOperationRelayer();
    }

    @Bean
    public OperationTransformations getOperationTransformations() {
        return new SimpleCharacterOperationTransformations();
    }

    @Bean
    public DocumentStore getDocumentStore() {
        return new SimpleHashMapDocumentStore(DocumentFormatterImpl::new);
    }

    public static void main(String[] args) {
        SpringApplication.run(CotextApplication.class, args);
    }

}
