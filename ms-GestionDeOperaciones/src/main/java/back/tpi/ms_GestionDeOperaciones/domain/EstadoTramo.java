package back.tpi.ms_GestionDeOperaciones.domain;

public enum EstadoTramo {
    PENDIENTE,      // Tramo aún no iniciado
    EN_CURSO,       // Tramo en ejecución
    COMPLETADO,     // Tramo finalizado
    CANCELADO       // Tramo cancelado
}