package com.nourane.shopflow.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title       = "ShopFlow API",
        version     = "1.0.0",
        description = "API REST complète pour la plateforme e-commerce ShopFlow — "
                    + "catalogue produits, panier, commandes, paiement simulé et gestion des stocks.",
        contact     = @Contact(name = "ShopFlow Team", email = "contact@shopflow.tn")
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "Serveur de développement"),
        @Server(url = "https://api.shopflow.tn", description = "Serveur de production")
    }
)
@SecurityScheme(
    name         = "bearerAuth",
    type         = SecuritySchemeType.HTTP,
    scheme       = "bearer",
    bearerFormat = "JWT",
    description  = "Entrez votre access_token JWT obtenu via POST /api/auth/login"
)
public class OpenApiConfig {
    // La configuration est portée par les annotations — aucun bean supplémentaire requis.
}