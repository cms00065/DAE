package es.ujaen.dae.notificacionincidencias.rest.dto;

import es.ujaen.dae.notificacionincidencias.entidades.Incidencia;
import org.springframework.stereotype.Service;

@Service
public class MapeadorIncidencia {
    public dtoIncidencia dto(Incidencia incidencia){
        return new dtoIncidencia(
                incidencia.id(),
                incidencia.fecha(),
                incidencia.fechaUltimaActualizacion(),
                incidencia.descripcion(),
                incidencia.localizacion(),
                incidencia.estado(),
                incidencia.ubicacionGPS(),
                incidencia.tipo(),
                incidencia.creador()
        );
    }

    public Incidencia entidad(dtoIncidencia dtoIncidencia){
        return new Incidencia(
                dtoIncidencia.fecha(),
                dtoIncidencia.descripcion(),
                dtoIncidencia.localizacion(),
                dtoIncidencia.estado(),
                dtoIncidencia.ubicacionGPS(),
                dtoIncidencia.tipo(),
                dtoIncidencia.creador()
        );
    }
}
