package back.tpi.APIGateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        http.csrf(csrf -> csrf.disable())
                .authorizeExchange(ex -> ex

                        .pathMatchers("/actuator/**").permitAll()

                        // ===== INFRAESTRUCTURA =====
                        .pathMatchers("/infraestructura/swagger-ui/**").permitAll()
                        .pathMatchers("/infraestructura/v3/api-docs").permitAll()
                        .pathMatchers("/infraestructura/v3/api-docs/**").permitAll()
                        .pathMatchers("/infraestructura/webjars/**").permitAll()

                        // ===== OPERACIONES =====
                        .pathMatchers("/operaciones/swagger-ui.html").permitAll()
                        .pathMatchers("/operaciones/swagger-ui/**").permitAll()
                        .pathMatchers("/operaciones/v3/api-docs/**").permitAll()
                        .pathMatchers("/operaciones/webjars/**").permitAll()

                        // ===== TRANSPORTE =====
                        .pathMatchers("/transporte/swagger-ui.html").permitAll()
                        .pathMatchers("/transporte/swagger-ui/**").permitAll()
                        .pathMatchers("/transporte/v3/api-docs/**").permitAll()
                        .pathMatchers("/transporte/webjars/**").permitAll()

                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(ServerHttpSecurity.OAuth2ResourceServerSpec::jwt);

        return http.build();
    }
}
