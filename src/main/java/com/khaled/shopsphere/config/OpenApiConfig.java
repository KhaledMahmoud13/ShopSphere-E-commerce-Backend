package com.khaled.shopsphere.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Spring security JWT asymmetric Encryption demo",
                        email = "khaled.ms113@gmail.com",
                        url = "https://github.com/KhaledMahmoud13"
                ),
                description = "OpenApi documentation for Spring Security project",
                title = "OpenApi Specification",
                version = "1.0",
                license = @License(
                        name = "Licence name",
                        url = "https://khaled/licence"
                ),
                termsOfService = "https://khaled/terms"
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local ENV"),
                @Server(url = "https://your-prod-url.com", description = "Prod ENV")
        },
        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
