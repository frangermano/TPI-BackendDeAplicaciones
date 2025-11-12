package back.tpi.ms_GestionDeTransporte.config;

import back.tpi.ms_GestionDeTransporte.domain.Camion;
import back.tpi.ms_GestionDeTransporte.domain.TipoCamion;
import back.tpi.ms_GestionDeTransporte.domain.Transportista;
import back.tpi.ms_GestionDeTransporte.repository.CamionRepository;
import back.tpi.ms_GestionDeTransporte.repository.TipoCamionRepository;
import back.tpi.ms_GestionDeTransporte.repository.TransportistaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Inicializador de datos para cargar tipos de camión, transportistas y camiones al arrancar la aplicación
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final TipoCamionRepository tipoCamionRepository;
    private final TransportistaRepository transportistaRepository;
    private final CamionRepository camionRepository;

    @Override
    @Transactional
    public void run(String... args) {
        // 1. Cargar tipos de camión
        if (tipoCamionRepository.count() == 0) {
            log.info("Inicializando tipos de camión...");
            cargarTiposCamion();
            log.info("Tipos de camión cargados exitosamente");
        } else {
            log.info("Los tipos de camión ya están cargados en la base de datos");
        }

        // 2. Cargar transportistas
        if (transportistaRepository.count() == 0) {
            log.info("Inicializando transportistas...");
            cargarTransportistas();
            log.info("Transportistas cargados exitosamente");
        } else {
            log.info("Los transportistas ya están cargados en la base de datos");
        }

        // 3. Cargar camiones
        if (camionRepository.count() == 0) {
            log.info("Inicializando camiones...");
            cargarCamiones();
            log.info("Camiones cargados exitosamente");
        } else {
            log.info("Los camiones ya están cargados en la base de datos");
        }
    }

    private void cargarTiposCamion() {
        List<TipoCamion> tiposCamion = Arrays.asList(
                TipoCamion.builder()
                        .nombre("Camión Ligero")
                        .capacidadVolumen(15.0)   // m³
                        .capacidadPeso(3500.0)    // kg (3.5 toneladas)
                        .build(),

                TipoCamion.builder()
                        .nombre("Camión Mediano")
                        .capacidadVolumen(30.0)   // m³
                        .capacidadPeso(8000.0)    // kg (8 toneladas)
                        .build(),

                TipoCamion.builder()
                        .nombre("Camión Pesado")
                        .capacidadVolumen(45.0)   // m³
                        .capacidadPeso(15000.0)   // kg (15 toneladas)
                        .build(),

                TipoCamion.builder()
                        .nombre("Camión Articulado")
                        .capacidadVolumen(60.0)   // m³
                        .capacidadPeso(25000.0)   // kg (25 toneladas)
                        .build(),

                TipoCamion.builder()
                        .nombre("Camión Tráiler")
                        .capacidadVolumen(90.0)   // m³
                        .capacidadPeso(40000.0)   // kg (40 toneladas)
                        .build(),

                TipoCamion.builder()
                        .nombre("Camión Refrigerado")
                        .capacidadVolumen(50.0)   // m³
                        .capacidadPeso(18000.0)   // kg (18 toneladas)
                        .build(),

                TipoCamion.builder()
                        .nombre("Camión Tolva")
                        .capacidadVolumen(35.0)   // m³
                        .capacidadPeso(20000.0)   // kg (20 toneladas)
                        .build(),

                TipoCamion.builder()
                        .nombre("Camión Cisterna")
                        .capacidadVolumen(40.0)   // m³
                        .capacidadPeso(22000.0)   // kg (22 toneladas)
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

    private void cargarTransportistas() {
        List<Transportista> transportistas = Arrays.asList(
                Transportista.builder()
                        .nombre("Juan Carlos Pérez")
                        .telefono("+54 351 234-5678")
                        .email("jperez@transportes.com")
                        .disponible(true)
                        .build(),

                Transportista.builder()
                        .nombre("María Fernanda González")
                        .telefono("+54 351 345-6789")
                        .email("mgonzalez@transportes.com")
                        .disponible(true)
                        .build(),

                Transportista.builder()
                        .nombre("Roberto Sánchez")
                        .telefono("+54 351 456-7890")
                        .email("rsanchez@transportes.com")
                        .disponible(true)
                        .build(),

                Transportista.builder()
                        .nombre("Ana Laura Rodríguez")
                        .telefono("+54 351 567-8901")
                        .email("arodriguez@transportes.com")
                        .disponible(true)
                        .build(),

                Transportista.builder()
                        .nombre("Diego Martínez")
                        .telefono("+54 351 678-9012")
                        .email("dmartinez@transportes.com")
                        .disponible(true)
                        .build(),

                Transportista.builder()
                        .nombre("Laura Beatriz Fernández")
                        .telefono("+54 351 789-0123")
                        .email("lfernandez@transportes.com")
                        .disponible(true)
                        .build(),

                Transportista.builder()
                        .nombre("Carlos Alberto López")
                        .telefono("+54 351 890-1234")
                        .email("clopez@transportes.com")
                        .disponible(false) // No disponible
                        .build(),

                Transportista.builder()
                        .nombre("Patricia Gómez")
                        .telefono("+54 351 901-2345")
                        .email("pgomez@transportes.com")
                        .disponible(true)
                        .build(),

                Transportista.builder()
                        .nombre("Miguel Ángel Díaz")
                        .telefono("+54 351 012-3456")
                        .email("mdiaz@transportes.com")
                        .disponible(true)
                        .build(),

                Transportista.builder()
                        .nombre("Silvia Raquel Torres")
                        .telefono("+54 351 123-4567")
                        .email("storres@transportes.com")
                        .disponible(true)
                        .build()
        );

        transportistaRepository.saveAll(transportistas);

        log.info("Se cargaron {} transportistas", transportistas.size());
        transportistas.forEach(t ->
                log.debug("- {} ({}) - Disponible: {}",
                        t.getNombre(),
                        t.getEmail(),
                        t.getDisponible())
        );
    }

    private void cargarCamiones() {
        // Obtener todos los transportistas y tipos de camión
        List<Transportista> transportistas = transportistaRepository.findAll();
        List<TipoCamion> tiposCamion = tipoCamionRepository.findAll();

        if (transportistas.isEmpty() || tiposCamion.isEmpty()) {
            log.warn("No se pueden cargar camiones: faltan transportistas o tipos de camión");
            return;
        }

        // Patentes argentinas realistas (formato: ABC123 o AB123CD)
        String[] patentes = {
                "ABC123", "DEF456", "GHI789", "JKL012", "MNO345",
                "PQR678", "STU901", "VWX234", "YZA567", "BCD890",
                "EFG123", "HIJ456", "KLM789", "NOP012", "QRS345",
                "TUV678", "WXY901", "ZAB234", "CDE567", "FGH890"
        };

        List<Camion> camiones = new ArrayList<>();

        for (int i = 0; i < patentes.length; i++) {
            // Distribuir transportistas y tipos de forma variada
            Transportista transportista = transportistas.get(i % transportistas.size());
            TipoCamion tipoCamion = tiposCamion.get(i % tiposCamion.size());

            // Costos realistas según el tipo de camión
            double costoCombustible = calcularCostoCombustible(tipoCamion);
            double costoKm = calcularCostoKm(tipoCamion);

            // Algunos camiones no disponibles (ya asignados)
            boolean disponible = (i % 5 != 0); // 20% no disponibles

            Camion camion = Camion.builder()
                    .patente(patentes[i])
                    .costoCombustible(costoCombustible)
                    .costoKm(costoKm)
                    .disponible(disponible)
                    .transportista(transportista)
                    .tipoCamion(tipoCamion)
                    .build();

            camiones.add(camion);
        }

        camionRepository.saveAll(camiones);

        log.info("Se cargaron {} camiones", camiones.size());
        camiones.forEach(c ->
                log.debug("- Patente: {} | Tipo: {} | Transportista: {} | Disponible: {}",
                        c.getPatente(),
                        c.getTipoCamion().getNombre(),
                        c.getTransportista().getNombre(),
                        c.getDisponible())
        );

        // Estadísticas
        long disponibles = camiones.stream().filter(Camion::getDisponible).count();
        log.info("Camiones disponibles: {}/{}", disponibles, camiones.size());
    }

    /**
     * Calcula costo de combustible realista según el tipo de camión
     * Basado en consumo promedio: camiones grandes consumen más
     */
    private double calcularCostoCombustible(TipoCamion tipo) {
        // Precio base del combustible (diesel): ~$800 por litro (Argentina 2024)
        double precioCombustible = 800.0;

        // Consumo estimado en litros/100km según capacidad
        double consumoBase;
        if (tipo.getCapacidadPeso() <= 5.0) {
            consumoBase = 25.0; // Camiones ligeros
        } else if (tipo.getCapacidadPeso() <= 12.0) {
            consumoBase = 35.0; // Camiones medianos
        } else if (tipo.getCapacidadPeso() <= 20.0) {
            consumoBase = 45.0; // Camiones pesados
        } else {
            consumoBase = 55.0; // Camiones muy pesados
        }

        // Costo por km = (consumo/100) * precio_litro
        return Math.round((consumoBase / 100.0) * precioCombustible * 100.0) / 100.0;
    }

    /**
     * Calcula costo operativo por km según el tipo de camión
     * Incluye desgaste, mantenimiento, seguros, etc.
     */
    private double calcularCostoKm(TipoCamion tipo) {
        // Costo base operativo según tamaño
        double costoBase;
        if (tipo.getCapacidadPeso() <= 5.0) {
            costoBase = 150.0;
        } else if (tipo.getCapacidadPeso() <= 12.0) {
            costoBase = 250.0;
        } else if (tipo.getCapacidadPeso() <= 20.0) {
            costoBase = 350.0;
        } else {
            costoBase = 500.0;
        }

        return Math.round(costoBase * 100.0) / 100.0;
    }
}