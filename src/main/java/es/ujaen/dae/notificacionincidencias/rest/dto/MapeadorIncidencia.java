package es.ujaen.dae.notificacionincidencias.rest.dto;

import es.ujaen.dae.notificacionincidencias.entidades.Incidencia;
import es.ujaen.dae.notificacionincidencias.entidades.TipoIncidencia;
import es.ujaen.dae.notificacionincidencias.entidades.Usuario;
import es.ujaen.dae.notificacionincidencias.excepciones.UsuarioNoDisponible;
import es.ujaen.dae.notificacionincidencias.repositorios.RepositorioIncidencia;
import es.ujaen.dae.notificacionincidencias.repositorios.RepositorioTipoIncidencia;
import es.ujaen.dae.notificacionincidencias.repositorios.RepositorioUsuarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class MapeadorIncidencia {

    @Autowired
    RepositorioUsuarios repositorioUsuarios;

    @Autowired
    RepositorioTipoIncidencia repositorioTipoIncidencia;

    public dtoIncidencia dto(Incidencia incidencia){
        return new dtoIncidencia(
                incidencia.id(),
                incidencia.fecha(),
                incidencia.fechaUltimaActualizacion(),
                incidencia.descripcion(),
                incidencia.localizacion(),
                incidencia.estado(),
                incidencia.ubicacionGPS(),
                Integer.toString(incidencia.tipo().id()),
                Integer.toString(incidencia.creador().id())
        );
    }

    public Incidencia entidad(dtoIncidencia dtoIncidencia){
        Usuario usuario = repositorioUsuarios.buscarID(Integer.parseInt(dtoIncidencia.IDcreador()))
                .orElseThrow(UsuarioNoDisponible::new);
        TipoIncidencia tipo = repositorioTipoIncidencia.buscar( Integer.parseInt(dtoIncidencia.IDtipoIncidencia()))
                .orElseThrow(UsuarioNoDisponible::new);

        return new Incidencia(
                LocalDate.now(),
                dtoIncidencia.descripcion(),
                dtoIncidencia.localizacion(),
                dtoIncidencia.estado(),
                dtoIncidencia.ubicacionGPS(),
                tipo,
                usuario
        );
    }
}
