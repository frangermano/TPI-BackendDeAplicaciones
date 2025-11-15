package back.tpi.ms_GestionDeInfraestructura.config;

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
import java.util.Map;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // URLs PUBLICAS de Keycloak para el navegador
        String publicAuthUrl = "http://localhost:8081/realms/tpi-backend/protocol/openid-connect/auth";
        String publicTokenUrl = "http://localhost:8081/realms/tpi-backend/protocol/openid-connect/token";

        return new OpenAPI()
                .info(new Info()
                        .title("API - Gestion de Infraestructura")
                        .version("1.0.0")
                        .description("""
                                ## Microservicio de Gestion de Infraestructura
                                
                                Este microservicio gestiona:
                                - Depositos
                                - Tarifas
                                
                                ### Roles disponibles:
                                - **CLIENTE**: Crear solicitudes y consultar estado
                                - **TRANSPORTISTA**: Registrar inicio/fin de tramos
                                - **ADMINISTRADOR**: Acceso completo al sistema
                                
                                ### Autenticacion:
                                Utiliza el boton **Authorize** para obtener un token JWT desde Keycloak.
                                """)
                        .contact(new Contact()
                                .name("Grupo 97 - Backend")
                                .email("desarrollo@empresa.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))

                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("API Gateway - Desarrollo")
                ))

                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Ingresa el token JWT obtenido desde Keycloak (sin el prefijo 'Bearer')"))

                        .addSecuritySchemes("keycloak-oauth2", new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .description("Autenticacion OAuth2 mediante Keycloak")
                                .flows(new OAuthFlows()
                                        .authorizationCode(new OAuthFlow()
                                                .authorizationUrl(publicAuthUrl)  // URL PUBLICA
                                                .tokenUrl(publicTokenUrl)          // URL PUBLICA
                                                .scopes(new Scopes()
                                                        .addString("openid", "OpenID Connect")
                                                        .addString("profile", "Informacion de perfil")
                                                        .addString("email", "Email del usuario")
                                                ))
                                ))
                )

                .addSecurityItem(new SecurityRequirement()
                        .addList("bearer-jwt")
                        .addList("keycloak-oauth2"));
    }
}