package com.nourane.shopflow.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title       = "ShopFlow API",
        version     = "1.0",
        description = "API REST du système de gestion de boutique en ligne ShopFlow",
        contact     = @Contact(name = "Dr. Ing. Ghada Feki", email = "ghada.feki@enis.tn")
    )
)
// ✅ AJOUT : déclaration du schéma JWT pour que le bouton "Authorize" fonctionne dans Swagger UI
@SecurityScheme(
    name   = "bearerAuth",
    type   = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class OpenApiConfig {}