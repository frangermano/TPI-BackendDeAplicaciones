package back.tpi.APIGateway.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final WebClient webClient = WebClient.create();

    @GetMapping("/api/login/oauth2/code/keycloak")
    public Mono<String> intercambiarCode(@RequestParam String code) throws UnsupportedEncodingException {
        String formData = "grant_type=authorization_code" +
                "&code=" + URLEncoder.encode(code, StandardCharsets.UTF_8) +
                "&client_id=tpi-backend-client" +
                "&redirect_uri=" + URLEncoder.encode("http://localhost:8080/api/login/oauth2/code/keycloak", StandardCharsets.UTF_8);

        return webClient.post()
                .uri("http://keycloak:8080/realms/tpi-backend/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .retrieve()
                .bodyToMono(String.class)
                .map(token -> {
                    log.info("🔐 Token recibido desde Keycloak: {}", token);
                    return "✅ Token recibido y logueado en consola";
                });
    }
}
