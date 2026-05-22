package com.aibert.dosw.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Academic Service API — AIBERT",
                version = "1.0",
                description = """
                        Manages academic data for AIBERT students: subjects, evaluation structures,\s
                        grades, simulation, academic goals, and student preferences.

                        **Key responsibilities:**
                        - Subject CRUD and evaluation cut configuration (AIB-13, AIB-14)
                        - Grade registration, editing, and weighted average calculation (AIB-15)
                        - Academic dashboard and global GPA across all subjects (AIB-9)
                        - Target grade simulation for pending evaluation cuts (AIB-17)
                        - Academic goal definition and real-time progress tracking (AIB-11)
                        - Study preferences configuration per student (AIB-12)
                        - Daily schedule availability configuration (AIB-10)

                        Authentication: all endpoints require a Bearer JWT token issued by the auth\s
                        service. Use the Authorize button to set your token.

                        Local testing: see `swagger-tests/swagger-tests-guide.md` in the repository\s
                        for ready-to-paste curl commands and a JWT token generation guide.

                        Contact dark-code-knights Team""",
                contact = @Contact(name = "dark-code-knights Team")
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {
}
