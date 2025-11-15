package back.tpi.ms_GestionDeOperaciones.dto;

import back.tpi.ms_GestionDeOperaciones.domain.EstadoSolicitud;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO que representa el estado completo de una solicitud de transporte, incluyendo cliente, contenedor y proceso operativo")
public class EstadoTransporteDTO {

    // ==========================
    //      CAMPOS OBLIGATORIOS
    // ==========================

    @Schema(
            description = "Identificador de la solicitud de transporte",
            example = "125"
    )
    private Long solicitudId;

    @Schema(
            description = "Estado actual de la solicitud",
            example = "EN_CAMINO"
    )
    private EstadoSolicitud estado;

    @Schema(
            description = "Mensaje detallado que describe el estado actual",
            example = "El camión ha salido del depósito y está en ruta hacia el destino"
    )
    private String mensajeEstado;

    // ---------- Cliente ----------

    @Schema(
            description = "Nombre completo del cliente que generó la solicitud",
            example = "Juan Rodríguez"
    )
    private String clienteNombre;

    @Schema(
            description = "Correo electrónico del cliente",
            example = "juan.rodriguez@example.com"
    )
    private String clienteEmail;

    // ---------- Contenedor ----------

    @Schema(
            description = "Identificador del contenedor asociado a la solicitud",
            example = "88"
    )
    private Long contenedorId;

    @Schema(
            description = "Peso total del contenedor en kilogramos",
            example = "750.5"
    )
    private Double contenedorPeso;

    @Schema(
            description = "Volumen total del contenedor en metros cúbicos",
            example = "12.3"
    )
    private Double contenedorVolumen;

    // ---------- Direcciones ----------

    @Schema(
            description = "Dirección de origen del traslado",
            example = "Av. Colón 1200, Córdoba"
    )
    private String direccionOrigen;

    @Schema(
            description = "Dirección de destino del traslado",
            example = "Av. Circunvalación 5400, Córdoba"
    )
    private String direccionDestino;

    // ---------- Fechas ----------

    @Schema(
            description = "Fecha en la que se creó la solicitud de transporte",
            example = "2025-03-15T14:30:00"
    )
    private LocalDateTime fechaSolicitud;

}