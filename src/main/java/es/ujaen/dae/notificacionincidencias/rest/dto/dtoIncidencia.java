package es.ujaen.dae.notificacionincidencias.rest.dto;

import es.ujaen.dae.notificacionincidencias.entidades.CoordenadasGPS;
import es.ujaen.dae.notificacionincidencias.entidades.EstadoIncidencia;
import es.ujaen.dae.notificacionincidencias.entidades.TipoIncidencia;
import es.ujaen.dae.notificacionincidencias.entidades.Usuario;

import java.time.LocalDate;

/**
 * @author jma00081
 */
public record dtoIncidencia(int id,
                            LocalDate fecha,
                            LocalDate fechaUltimaActualizacion,
                            String descripcion,
                            String localizacion,
                            EstadoIncidencia estado,
                            CoordenadasGPS ubicacionGPS,
                            String IDtipoIncidencia,
                            String IDcreador,
                            byte[] foto) {


}
