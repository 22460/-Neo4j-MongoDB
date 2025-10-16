package com.xys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.License;

@SpringBootApplication
@EnableNeo4jRepositories("com.xys.repository")
@OpenAPIDefinition(
    info = @Info(
        title = "Neo4j 人物关系 API",
        description = "这是一个基于Spring Boot和Neo4j的人物关系管理系统API",
        version = "1.0.0",
        contact = @Contact(
            name = "系统管理员",
            email = "admin@example.com"
        ),
        license = @License(
            name = "Apache 2.0",
            url = "http://springdoc.org"
        )
    )
)
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
