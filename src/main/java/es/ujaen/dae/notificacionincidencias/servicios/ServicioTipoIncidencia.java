package es.ujaen.dae.notificacionincidencias.servicios;

import es.ujaen.dae.notificacionincidencias.entidades.Incidencia;
import es.ujaen.dae.notificacionincidencias.entidades.Rol;
import es.ujaen.dae.notificacionincidencias.entidades.TipoIncidencia;
import es.ujaen.dae.notificacionincidencias.entidades.Usuario;
import es.ujaen.dae.notificacionincidencias.excepciones.TipoIncidenciaEstaEnUso;
import es.ujaen.dae.notificacionincidencias.excepciones.TipoIncidenciaYaExiste;
import es.ujaen.dae.notificacionincidencias.excepciones.UsuarioNoEsAdmin;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author cms00065@red.ujaen.es
 */
@Service
public class ServicioTipoIncidencia {
    Map<Integer, TipoIncidencia> tiposIncidencias;

    public ServicioTipoIncidencia() {
        tiposIncidencias = new HashMap<Integer, TipoIncidencia>();
    }

    /**
     * @brief Creación de un nuevo tipo de incidencia
     * @details Solo el Usuario de tipo "ADMIN" puede dar de alta un tipo
     * @param usuario Usuario que ejecuta la operación
     * @param tipoNuevo Tipo de incidencia a registrar
     * @return El tipo de incidencia con su fecha de alta
     */
    public TipoIncidencia alta(@Valid Usuario usuario, @Valid TipoIncidencia tipoNuevo){
        if(usuario.rol() != Rol.ADMIN){
            throw new UsuarioNoEsAdmin();
        }

        if(tiposIncidencias.containsKey(tipoNuevo.id())){
            throw new TipoIncidenciaYaExiste();
        }

        tipoNuevo.isActivo(true);
        tipoNuevo.fechaAlta(LocalDateTime.now());
        tiposIncidencias.put(tipoNuevo.id(), tipoNuevo);
        return tipoNuevo;
    }

    /**
     * @brief Eliminación (marcar como que no está activa) de un tipo de incidencia
     * @details Solo el Usuario de tipo "ADMIN" puede dar de baja un tipo
     * @param usuario Usuario que ejecuta la operación
     * @param tipoId Identificador del tipo a eliminar
     * @param incidencias Lista de incidencias existentes para comprobar su uso
     */
    public void baja(@Valid Usuario usuario, int tipoId, List<Incidencia> incidencias){
        if(usuario.rol() != Rol.ADMIN){
            throw new UsuarioNoEsAdmin();
        }

        boolean enUso = false;
        for(Incidencia incidencia : incidencias){
            if(incidencia.tipo().id() == tipoId && incidencia.estado() != null){
                enUso = true;
                break;
            }
        }

        //Si el tipo de incidencia está en uso no puede eliminarse
        if(enUso){
            throw new TipoIncidenciaEstaEnUso();
        }

        TipoIncidencia tipo = tiposIncidencias.get(tipoId);
        if (tipo != null) {
            tipo.isActivo(false); //"Elimino" (marco como que no está activa) el tipo de incidencia correspondiente
        }

    }

    public List<TipoIncidencia> listarActivos(){
        List<TipoIncidencia> tiposActivos = new ArrayList<TipoIncidencia>();
        for (TipoIncidencia tipoIncidencia : tiposIncidencias.values()) {
            if (tipoIncidencia.isActivo()) {
                tiposActivos.add(tipoIncidencia);
            }
        }
        return tiposActivos;
    }

    /**
     * @brief Busca un tipo de incidencia por su id
     * @param id identificador del tipo de incidencia
     * @return Devuelve el tipo de incidencia correspondiente o vacío si no existe
     */
    public Optional<TipoIncidencia> buscarPorId(int id){
        return Optional.ofNullable(tiposIncidencias.get(id));
    }
}
