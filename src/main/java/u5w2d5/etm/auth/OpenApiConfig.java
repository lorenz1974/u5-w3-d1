package u5w2d5.etm.auth;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    /**
     * Configura l'API OpenAPI con uno schema di sicurezza basato su JWT.
     *
     * Questo metodo crea un bean OpenAPI personalizzato che aggiunge uno schema di
     * sicurezza
     * di tipo HTTP con schema "bearer" e formato "JWT". Questo schema di sicurezza
     * viene utilizzato
     * per autenticare le richieste API utilizzando token JWT.
     *
     * @return un'istanza di OpenAPI configurata con lo schema di sicurezza JWT.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        // Definisce il nome dello schema di sicurezza come "bearerAuth"
        final String securitySchemeName = "bearerAuth";

        // Crea una nuova istanza di OpenAPI e configura le impostazioni di sicurezza
        return new OpenAPI()
                // Aggiunge un elemento di sicurezza con il nome dello schema di sicurezza
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                // Configura i componenti dell'API, aggiungendo lo schema di sicurezza
                .components(new Components().addSecuritySchemes(securitySchemeName,
                        new SecurityScheme()
                                // Imposta il tipo di schema di sicurezza come HTTP
                                .type(SecurityScheme.Type.HTTP)
                                // Specifica che lo schema di sicurezza utilizza il formato "bearer"
                                .scheme("bearer")
                                // Definisce il formato del token come JWT (JSON Web Token)
                                .bearerFormat("JWT")
                                // Aggiunge una descrizione per lo schema di sicurezza
                                .description("Inserisci il token JWT nel formato: Bearer {token}")));
    }
}