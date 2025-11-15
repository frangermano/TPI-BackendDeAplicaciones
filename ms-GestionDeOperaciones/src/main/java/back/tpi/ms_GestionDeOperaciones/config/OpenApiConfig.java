package back.tpi.ms_GestionDeOperaciones.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:http://localhost:8080/realms/tpi-backend}")
    private String issuerUri;

    @Bean
    public OpenAPI customOpenAPI() {
        // URLs de Keycloak
        String authUrl = issuerUri + "/protocol/openid-connect/auth";
        String tokenUrl = issuerUri + "/protocol/openid-connect/token";

        return new OpenAPI()
                .info(new Info()
                        .title("API - Gestión de Operaciones")
                        .version("1.0.0")
                        .description("""
                                ## Microservicio de Gestión de Operaciones
                                
                                Este microservicio gestiona:
                                - ✅ Solicitudes de traslado completas
                                - 🚚 Asignación de rutas y tramos
                                - 📊 Consulta de estado de transportes
                                - 💰 Cálculo de costos reales
                                - 🗺️ Generación de rutas tentativas
                                
                                ### Roles disponibles:
                                - **CLIENTE**: Crear solicitudes y consultar estado
                                - **TRANSPORTISTA**: Registrar inicio/fin de tramos
                                - **ADMINISTRADOR**: Acceso completo al sistema
                                
                                ### Autenticación:
                                Utiliza el botón **Authorize** para obtener un token JWT desde Keycloak.
                                """)
                        .contact(new Contact()
                                .name("Grupo 97 - Backend")
                                .email("desarrollo@empresa.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))

                .servers(List.of(
                        new Server()
                                .url("http://localhost:8083")
                                .description("Servidor local de desarrollo"),
                        new Server()
                                .url("http://ms-gestiondeoperaciones:8083")
                                .description("Servidor Docker")
                ))

                .components(new Components()
                        // ===== OPCIÓN 1: Bearer JWT (Manual) =====
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Ingresa el token JWT obtenido desde Keycloak (sin el prefijo 'Bearer')"))

                        // ===== OPCIÓN 2: OAuth2 (Automático desde Keycloak) =====
                        .addSecuritySchemes("keycloak-oauth2", new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .description("Autenticación OAuth2 mediante Keycloak")
                                .flows(new OAuthFlows()
                                        .authorizationCode(new OAuthFlow()
                                                .authorizationUrl(authUrl)
                                                .tokenUrl(tokenUrl)
                                                .scopes(new Scopes()
                                                        .addString("openid", "OpenID Connect")
                                                        .addString("profile", "Información de perfil")
                                                        .addString("email", "Email del usuario")
                                                ))
                                ))
                )

                // Aplicar seguridad a TODOS los endpoints por defecto
                .addSecurityItem(new SecurityRequirement()
                        .addList("bearer-jwt")
                        .addList("keycloak-oauth2"));
    }
}