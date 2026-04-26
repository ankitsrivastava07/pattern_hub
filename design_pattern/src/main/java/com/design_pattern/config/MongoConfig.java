package com.design_pattern.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {

    // Create a MongoClient bean
    @Bean
    public MongoClient mongoClient() {
        // Replace with your connection string
        return MongoClients.create("mongodb://localhost:27017");
    }

    // Create a MongoTemplate bean
    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        // Replace "test" with your database name
        return new MongoTemplate(mongoClient, "test");
    }
}
