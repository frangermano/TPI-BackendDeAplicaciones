package back.tpi.APIGateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

/**
 * Configuración avanzada de CORS para el API Gateway
 * Esta configuración es opcional si ya tienes CORS configurado en application.yml
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        // Permitir todos los orígenes (en producción deberías especificar los dominios exactos)
        corsConfig.setAllowedOrigins(Collections.singletonList("*"));

        // Permitir todos los métodos HTTP
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // Permitir todos los headers
        corsConfig.setAllowedHeaders(Collections.singletonList("*"));

        // Permitir credenciales
        // corsConfig.setAllowCredentials(true);

        // Tiempo de caché de la configuración CORS
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}
