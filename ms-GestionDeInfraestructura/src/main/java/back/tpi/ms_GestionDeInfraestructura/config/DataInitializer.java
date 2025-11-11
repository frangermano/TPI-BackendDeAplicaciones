package back.tpi.ms_GestionDeInfraestructura.config;

import back.tpi.ms_GestionDeInfraestructura.domain.Deposito;
import back.tpi.ms_GestionDeInfraestructura.repository.DepositoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Inicializador de datos para cargar depósitos al arrancar la aplicación.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final DepositoRepository depositoRepository;

    @Override
    public void run(String... args) {
        if (depositoRepository.count() == 0) {
            log.info("Inicializando depósitos...");
            cargarDepositos();
            log.info("Depósitos cargados exitosamente.");
        } else {
            log.info("Los depósitos ya están cargados en la base de datos.");
        }
    }

    private void cargarDepositos() {
        List<Deposito> depositos = Arrays.asList(
                // Córdoba
                Deposito.builder()
                        .nombre("Depósito Central Córdoba")
                        .direccion("Av. Circunvalación 1500")
                        .latitud(-31.4135)
                        .longitud(-64.1881)
                        .costoEstadia(5000)
                        .build(),

                // Entre Córdoba y Buenos Aires
                Deposito.builder()
                        .nombre("Depósito Rosario Norte")
                        .direccion("Ruta 9 Km 305")
                        .latitud(-32.9442)
                        .longitud(-60.6505)
                        .costoEstadia(6000)
                        .build(),

                Deposito.builder()
                        .nombre("Depósito Villa María")
                        .direccion("Ruta Nacional 9 Km 560")
                        .latitud(-32.4072)
                        .longitud(-63.2405)
                        .costoEstadia(5000)
                        .build(),

                // Buenos Aires
                Deposito.builder()
                        .nombre("Depósito Buenos Aires Sur")
                        .direccion("Av. Gral. Paz 12000")
                        .latitud(-34.7000)
                        .longitud(-58.4800)
                        .costoEstadia(8000)
                        .build(),

                Deposito.builder()
                        .nombre("Depósito La Plata")
                        .direccion("Ruta 2 Km 50")
                        .latitud(-34.9205)
                        .longitud(-57.9536)
                        .costoEstadia(10000)
                        .build(),

                // Norte
                Deposito.builder()
                        .nombre("Depósito Santiago del Estero")
                        .direccion("Ruta 9 Norte Km 1100")
                        .latitud(-27.7834)
                        .longitud(-64.2642)
                        .costoEstadia(6000)
                        .build(),

                // Mendoza
                Deposito.builder()
                        .nombre("Depósito Mendoza Este")
                        .direccion("Acceso Este 500")
                        .latitud(-32.8895)
                        .longitud(-68.8458)
                        .costoEstadia(7000)
                        .build()
        );

        depositoRepository.saveAll(depositos);

        log.info("Se cargaron {} depósitos", depositos.size());
        depositos.forEach(dep ->
                log.debug("- {} ({}, lat: {}, long: {}, costo: ${})",
                        dep.getNombre(),
                        dep.getDireccion(),
                        dep.getLatitud(),
                        dep.getLongitud(),
                        dep.getCostoEstadia())
        );
    }
}
