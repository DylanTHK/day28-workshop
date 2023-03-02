package com.day28.workshop28.config;

// COPY FROM HERE ONWARDS
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

// update constants
import static com.day28.workshop28.Constants.*;

@Configuration
public class AppConfig {
    
    @Value("${mongo.url}")
    private String connectionString;

    @Bean
    public MongoTemplate mongoTemplate() {
        
        MongoClient client = MongoClients.create(connectionString);
        return new MongoTemplate(client, DB_BGG);
    }

}

