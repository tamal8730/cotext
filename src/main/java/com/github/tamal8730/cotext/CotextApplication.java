package com.github.tamal8730.cotext;

import com.github.tamal8730.cotext.model.DocStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CotextApplication {

    @Bean
    public DocStore getDocStore() {
        return new DocStore();
    }

    public static void main(String[] args) {
        SpringApplication.run(CotextApplication.class, args);
    }

}
