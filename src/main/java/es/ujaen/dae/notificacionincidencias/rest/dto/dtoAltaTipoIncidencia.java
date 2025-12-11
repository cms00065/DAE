package es.ujaen.dae.notificacionincidencias.rest.dto;

/**
 * @author cms00065
 */
public record dtoAltaTipoIncidencia (
        dtoUsuario usuario,
        dtoTipoIncidencia tipo
) {
}
