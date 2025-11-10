package es.ujaen.dae.notificacionincidencias.servicios;

import es.ujaen.dae.notificacionincidencias.entidades.*;
import es.ujaen.dae.notificacionincidencias.excepciones.*;
import es.ujaen.dae.notificacionincidencias.repositorios.RepositorioIncidencia;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;

/**
 * @author jma00081
 */
@Service
@Validated
public class ServicioIncidencias {
    @Autowired
    RepositorioIncidencia repositorioIncidencias;

    public ServicioIncidencias() {

    }

    /**
     * Crea una nueva incidencia
     * @param nuevaIncidencia La incidencia a crear
     * @throws IncidenciaYaCreada Si la incidencia ya etsaba registrada
     */
    public void crearIncidencia(@Valid Incidencia nuevaIncidencia) {
        repositorioIncidencias.guardar(nuevaIncidencia);
    }

    /**
     * Busca incidencias según los filtros proporcionados
     * @param actor El usuario cuyas incidencias se quieres buscar
     * @return La lista de incidencias que cumplen los filtros
     */
    public List<Incidencia> buscarIncidenciasCreadasPor(Usuario actor) {
        return repositorioIncidencias.buscarIncidenciasCreadasPor(actor);
    }

    /**
     * Filtra las incidencias por tipo
     * @param tipo El tipo por el que queremos filtrar
     * @return Lista de incidencias cuyo tipo sea el buscado
     */
    public List<Incidencia> buscarIncidenciasPorTipo(TipoIncidencia tipo) {
        return repositorioIncidencias.buscarPorTipo(tipo);
    }

    /**
     * Filtra las incidencias por estado
     * @param estado El estado por el que queremos filtrar
     * @return Lista de incidencias del estado buscado
     */
    public List<Incidencia> buscarIncidenciasPorEstado(EstadoIncidencia estado) {
        return repositorioIncidencias.buscarPorEstado(estado);
    }

    /**
     * Borra una incidencia si el usuario actor tiene permiso para hacerlo
     * @param actor El usuario que solicita el borrado
     * @param incidencia La incidencia a borrar
     */
    public void borrar(Usuario actor, @Valid Incidencia incidencia) {
        if (incidencia != null && incidencia.puedeBorrar(actor)) {
            repositorioIncidencias.eliminar(incidencia);
        }
    }

    /**
     * Cambia el estado de una incidencia
     * @param actor El usuario que solicita el cambio de estado
     * @throws UsuarioNoEsAdmin Si el usuario no es ADMIN
     * @throws IncidenciaNoDisponible si la incidencia no está registrada
     * @param incidencia La incidencia a modificar
     * @param nuevoEstado El nuevo estado que se asignará a la incidencia
     */
    @Transactional
    public void cambiarEstado(Usuario actor, @Valid Incidencia incidencia, EstadoIncidencia nuevoEstado) {
        if (actor.rol() != Rol.ADMIN) {
            throw new UsuarioNoEsAdmin();
        }

        Optional<Incidencia> incidenciaExistente = repositorioIncidencias.buscarPorId(incidencia.id());
        if (incidenciaExistente.isEmpty()) {
            throw new IncidenciaNoDisponible();
        }

        repositorioIncidencias.actualizarEstado(incidencia, nuevoEstado);
    }
}
