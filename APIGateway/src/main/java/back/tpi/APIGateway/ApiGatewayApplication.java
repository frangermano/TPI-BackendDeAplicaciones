package back.tpi.APIGateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/*
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

 */

@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    /**
     * Configuración alternativa de rutas usando código Java
     * (opcional, ya tenemos la configuración en application.yml)
     */
    // @Bean
    // public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
    //     return builder.routes()
    //         .route("tarifas-service", r -> r
    //             .path("/api/tarifas/**")
    //             .uri("http://localhost:8081"))
    //         .route("contenedores-service", r -> r
    //             .path("/api/contenedores/**")
    //             .uri("http://localhost:8082"))
    //         .route("solicitudes-service", r -> r
    //             .path("/api/solicitudes-traslado/**")
    //             .uri("http://localhost:8083"))
    //         .build();
    // }
}