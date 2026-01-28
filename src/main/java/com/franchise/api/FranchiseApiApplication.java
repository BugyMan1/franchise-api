package com.franchise.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories
public class FranchiseApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FranchiseApiApplication.class, args);
    }
}
