package back.tpi.ms_GestionDeOperaciones.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Configuration
public class CamionClient {

    @Bean
    RestClient restClient(@Value("${api.base-url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    /*
    try {
        var response = restClient.get()
                .uri("/api/proveedores/99")
                .retrieve()
                .body(ProveedorDTO.class);
    } catch (
    RestClientResponseException ex) {
        System.err.println("Error: " + ex.getStatusCode());
        System.err.println("Respuesta: " + ex.getResponseBodyAsString());
    }

     */
}
