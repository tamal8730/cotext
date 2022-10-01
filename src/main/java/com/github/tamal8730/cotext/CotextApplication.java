package com.github.tamal8730.cotext;

import com.github.tamal8730.cotext.config.HttpHandshakeInterceptor;
import com.github.tamal8730.cotext.document_formatter.DocumentFormatter;
import com.github.tamal8730.cotext.document_formatter.impl.DocumentFormatterImpl;
import com.github.tamal8730.cotext.model.DocStateStore;
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
    public DocStateStore getDocStateStore() {
        return new DocStateStore(DocumentFormatterImpl::new);
    }


    public static void main(String[] args) {
        SpringApplication.run(CotextApplication.class, args);
    }

}
