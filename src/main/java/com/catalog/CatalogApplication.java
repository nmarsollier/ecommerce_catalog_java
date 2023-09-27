package com.catalog;

import com.catalog.rabbit.ConsumeCatalog;
import com.catalog.rabbit.ConsumeLogout;
import com.catalog.rabbit.ConsumeOrderPlaced;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CatalogApplication {
    @Autowired
    ConsumeCatalog consumeCatalog;

    @Autowired
    ConsumeOrderPlaced consumeOrderPlaced;

    @Autowired
    ConsumeLogout consumeLogout;

    @PostConstruct
    public void init() {
        consumeCatalog.init();
        consumeOrderPlaced.init();
        consumeLogout.init();
    }

    public static void main(String[] args) {
        SpringApplication.run(CatalogApplication.class, args);
    }
}
