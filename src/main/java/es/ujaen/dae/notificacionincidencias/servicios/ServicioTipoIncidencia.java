package es.ujaen.dae.notificacionincidencias.servicios;

import es.ujaen.dae.notificacionincidencias.entidades.Incidencia;
import es.ujaen.dae.notificacionincidencias.entidades.Rol;
import es.ujaen.dae.notificacionincidencias.entidades.TipoIncidencia;
import es.ujaen.dae.notificacionincidencias.entidades.Usuario;
import es.ujaen.dae.notificacionincidencias.excepciones.TipoIncidenciaEstaEnUso;
import es.ujaen.dae.notificacionincidencias.excepciones.TipoIncidenciaYaExiste;
import es.ujaen.dae.notificacionincidencias.excepciones.UsuarioNoEsAdmin;
import es.ujaen.dae.notificacionincidencias.repositorios.RepositorioTipoIncidencia;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author cms00065
 */
@Service
public class ServicioTipoIncidencia {
    @Autowired
    RepositorioTipoIncidencia repositorioTipoIncidencia;

    public ServicioTipoIncidencia() {

    }

    /**
     * @brief Creación de un nuevo tipo de incidencia
     * @details Solo el Usuario de tipo "ADMIN" puede dar de alta un tipo
     * @param usuario Usuario que ejecuta la operación
     * @param tipoNuevo Tipo de incidencia a registrar
     * @return El tipo de incidencia con su fecha de alta
     */
    public TipoIncidencia alta(@Valid Usuario usuario, @Valid TipoIncidencia tipoNuevo) {
        if (usuario.rol() != Rol.ADMIN) {
            throw new UsuarioNoEsAdmin();
        }

        if (repositorioTipoIncidencia.buscarPorNombre(tipoNuevo.nombre()).isPresent()) {
            throw new TipoIncidenciaYaExiste();
        }

        tipoNuevo.isActivo(true);
        tipoNuevo.fechaAlta(LocalDateTime.now());
        repositorioTipoIncidencia.crear(tipoNuevo);
        return tipoNuevo;
    }

    /**
     * @brief Eliminación (marcar como que no está activa) de un tipo de incidencia
     * @details Solo el Usuario de tipo "ADMIN" puede dar de baja un tipo
     * @param usuario Usuario que ejecuta la operación
     * @param tipoId Identificador del tipo a eliminar
     * @param incidencias Lista de incidencias existentes para comprobar su uso
     */
    public void baja(@Valid Usuario usuario, int tipoId, List<Incidencia> incidencias) {
        if (usuario.rol() != Rol.ADMIN) {
            throw new UsuarioNoEsAdmin();
        }

        //Busco la entidad gestionada
        Optional<TipoIncidencia> tipo = repositorioTipoIncidencia.buscar(tipoId);

        boolean enUso = false;
        for (Incidencia incidencia : incidencias) {
            if (incidencia.tipo().id() == tipoId && incidencia.estado() != null) {
                enUso = true;
                break;
            }
        }

        //Si el tipo de incidencia está en uso no puede eliminarse
        if (enUso) {
            throw new TipoIncidenciaEstaEnUso();
        }

        tipo.get().isActivo(false); //"Elimino lógicamente" (marco como que no está activa) el tipo de incidencia correspondiente
        repositorioTipoIncidencia.actualizar(tipo.get());
    }

    public List<TipoIncidencia> listarActivos() {
        return repositorioTipoIncidencia.listarActivos();
    }

    /**
     * @brief Busca un tipo de incidencia por su id
     * @param id identificador del tipo de incidencia
     * @return Devuelve el tipo de incidencia correspondiente o vacío si no existe
     */
    public Optional<TipoIncidencia> buscarPorId(int id) {
        return repositorioTipoIncidencia.buscar(id);
    }

    /**
     * @brief Busca un tipo de incidencia por su nombre
     * @param nombre Nombre del tipo de incidencia
     * @return Devuelve el tipo de incidencia correspondiente o vacío si no existe
     */
    public Optional<TipoIncidencia> buscarPorNombre(String nombre) {
        return repositorioTipoIncidencia.buscarPorNombre(nombre);
    }
}
