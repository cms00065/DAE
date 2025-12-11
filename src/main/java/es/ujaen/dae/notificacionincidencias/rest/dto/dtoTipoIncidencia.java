package es.ujaen.dae.notificacionincidencias.rest.dto;

import java.time.LocalDateTime;

/**
 * @author cms00065
 */
public record dtoTipoIncidencia(int id,
                                String nombre,
                                String descripcion,
                                boolean activo,
                                LocalDateTime fechaAlta) {

}
