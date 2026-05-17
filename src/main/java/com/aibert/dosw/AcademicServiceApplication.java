package com.aibert.dosw;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
    info = @Info(
        title = "Academic Service API",
        version = "1.0.0",
        description = "Module 2 — Academic Management for the A.IBERT ECI Planner system. " +
                      "Allows registering subjects, configuring evaluation structures, " +
                      "registering grades and automatically calculating averages.",
        contact = @Contact(
            name = "Dark Code Knights",
            url = "https://github.com/AI-BERT-BACKEND/dark-code-knights-academic-service"
        )
    )
)
@SpringBootApplication
public class AcademicServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AcademicServiceApplication.class, args);
    }
}
