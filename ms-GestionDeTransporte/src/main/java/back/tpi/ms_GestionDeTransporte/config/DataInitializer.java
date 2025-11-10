package back.tpi.ms_GestionDeTransporte.config;

import back.tpi.ms_GestionDeTransporte.domain.TipoCamion;
import back.tpi.ms_GestionDeTransporte.repository.TipoCamionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Inicializador de datos para cargar tipos de camión al arrancar la aplicación
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final TipoCamionRepository tipoCamionRepository;

    @Override
    public void run(String... args) {
        if (tipoCamionRepository.count() == 0) {
            log.info("Inicializando tipos de camión...");
            cargarTiposCamion();
            log.info("Tipos de camión cargados exitosamente");
        } else {
            log.info("Los tipos de camión ya están cargados en la base de datos");
        }
    }

    private void cargarTiposCamion() {
        List<TipoCamion> tiposCamion = Arrays.asList(
                TipoCamion.builder()
                        .nombre("Camión Ligero")
                        .capacidadVolumen(15.0)  // m³
                        .capacidadPeso(3.5)      // toneladas
                        .build(),

                TipoCamion.builder()
                        .nombre("Camión Mediano")
                        .capacidadVolumen(30.0)  // m³
                        .capacidadPeso(8.0)      // toneladas
                        .build(),

                TipoCamion.builder()
                        .nombre("Camión Pesado")
                        .capacidadVolumen(45.0)  // m³
                        .capacidadPeso(15.0)     // toneladas
                        .build(),

                TipoCamion.builder()
                        .nombre("Camión Articulado")
                        .capacidadVolumen(60.0)  // m³
                        .capacidadPeso(25.0)     // toneladas
                        .build(),

                TipoCamion.builder()
                        .nombre("Camión Tráiler")
                        .capacidadVolumen(90.0)  // m³
                        .capacidadPeso(40.0)     // toneladas
                        .build(),

                TipoCamion.builder()
                        .nombre("Camión Refrigerado")
                        .capacidadVolumen(50.0)  // m³
                        .capacidadPeso(18.0)     // toneladas
                        .build(),

                TipoCamion.builder()
                        .nombre("Camión Tolva")
                        .capacidadVolumen(35.0)  // m³
                        .capacidadPeso(20.0)     // toneladas
                        .build(),

                TipoCamion.builder()
                        .nombre("Camión Cisterna")
                        .capacidadVolumen(40.0)  // m³
                        .capacidadPeso(22.0)     // toneladas
                        .build()
        );

        tipoCamionRepository.saveAll(tiposCamion);

        log.info("Se cargaron {} tipos de camión", tiposCamion.size());
        tiposCamion.forEach(tipo ->
                log.debug("- {} (Vol: {}m³, Peso: {}t)",
                        tipo.getNombre(),
                        tipo.getCapacidadVolumen(),
                        tipo.getCapacidadPeso())
        );
    }
}