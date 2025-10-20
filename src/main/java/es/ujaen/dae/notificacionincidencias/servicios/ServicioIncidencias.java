package es.ujaen.dae.notificacionincidencias.servicios;

import es.ujaen.dae.notificacionincidencias.entidades.*;
import es.ujaen.dae.notificacionincidencias.excepciones.UsuarioNoDisponible;
import es.ujaen.dae.notificacionincidencias.excepciones.UsuarioNoEsAdmin;
import es.ujaen.dae.notificacionincidencias.excepciones.UsuarioNoLogueado;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author jma00081
 */
@Service
@Validated
public class ServicioIncidencias {
    Map<Integer, TipoIncidencia> tiposIncidencia;
    Map<Integer, Incidencia> incidenciasCreadas;
    Map<Integer, List<Incidencia>> incidenciasPorUsuario;

    EstadoIncidencia filtroEstado;

    //Usuario usuarioActual;

    public ServicioIncidencias() {
        tiposIncidencia = new HashMap<>();
        incidenciasCreadas = new HashMap<>();
        incidenciasPorUsuario = new HashMap<>();
        filtroEstado = null;
    }

    /**
     * Crea una nueva incidencia
     * @param actor El usuario que crea la incidencia
     * @throws UsuarioNoLogueado Si el usuario no ha iniciado sesión
     * @param nuevaIncidencia La incidencia a crear
     */
    public void crearIncidencia(Usuario actor, @Valid Incidencia nuevaIncidencia) {
        // Comprobar que el usuario que crea la incidencia ha iniciado sesión
        if (actor.login() == null) {
            throw new UsuarioNoLogueado();
        }

        TipoIncidencia tipo = nuevaIncidencia.tipo();
        incidenciasCreadas.put(nuevaIncidencia.id(), nuevaIncidencia);
        tiposIncidencia.put(tipo.id(), tipo);
        incidenciasPorUsuario.computeIfAbsent(actor.id(), k -> new java.util.ArrayList<>()).add(nuevaIncidencia);
    }

    /**
     * Lista las incidencias creadas por el usuario actor
     * @param actor El usuario que solicita la lista
     * @throws UsuarioNoLogueado Si el usuario no ha iniciado sesión
     * @return La lista de incidencias creadas por el usuario
     */
    public List<Incidencia> listarMisInicidencias(Usuario actor) {
        if (actor.login() == null) {
            throw new UsuarioNoLogueado();
        }

        return incidenciasPorUsuario.getOrDefault(actor.id(), List.of());
    }

    /**
     * Busca incidencias según los filtros proporcionados
     * @param actor El usuario que realiza la búsqueda
     * @throws UsuarioNoLogueado Si el usuario no ha iniciado sesión
     * @throws UsuarioNoEsAdmin Si el usuario no es ADMIN
     * @param tipoId El id del tipo de incidencia a filtrar (0 para no filtrar por tipo)
     * @param estado El estado de la incidencia a filtrar (null para no filtrar por estado)
     * @return La lista de incidencias que cumplen los filtros
     */
    public List<Incidencia> buscar(Usuario actor, @PositiveOrZero int tipoId, EstadoIncidencia estado) {
        if (actor.login() == null) {
            throw new UsuarioNoLogueado();
        }

        if (actor.rol() != Rol.ADMIN) {
            throw new UsuarioNoEsAdmin();
        }

        return incidenciasPorUsuario.getOrDefault(actor.id(), List.of()).stream()
                .filter(incidencia -> (tipoId == 0 || incidencia.tipo().id() == tipoId) &&
                        (estado == null || incidencia.estado() == estado))
                .toList();
    }

    /**
     * Borra una incidencia si el usuario actor tiene permiso para hacerlo
     * @param actor El usuario que solicita el borrado
     * @throws UsuarioNoLogueado Si el usuario no ha iniciado sesión
     * @param incidenciaId El id de la incidencia a borrar
     */
    public void borrar(Usuario actor, @Positive int incidenciaId) {
        if (actor.login() == null) {
            throw new UsuarioNoLogueado();
        }

        Incidencia incidencia = incidenciasCreadas.get(incidenciaId);
        if (incidencia != null && incidencia.puedeBorrar(actor)) {
            incidenciasCreadas.remove(incidenciaId);
            incidenciasPorUsuario.getOrDefault(actor.id(), List.of()).remove(incidencia);
        }
    }

    /**
     * Cambia el estado de una incidencia
     * @param actor El usuario que solicita el cambio de estado
     * @throws UsuarioNoLogueado Si el usuario no ha iniciado sesión
     * @throws UsuarioNoEsAdmin Si el usuario no es ADMIN
     * @param incidenciaId El id de la incidencia a modificar
     * @param nuevoEstado El nuevo estado que se asignará a la incidencia
     * @return La incidencia modificada, o un Optional vacío si no se encontró la incidencia
     */
    public Optional<Incidencia> cambiarEstado(Usuario actor, @Positive int incidenciaId, EstadoIncidencia nuevoEstado) {
        if (actor.login() == null) {
            throw new UsuarioNoLogueado();
        }

        if (actor.rol() != Rol.ADMIN) {
            throw new UsuarioNoEsAdmin();
        }

        Incidencia incidencia = incidenciasCreadas.get(incidenciaId);
        if (incidencia != null) {
            incidencia.cambiarEstado(nuevoEstado);
        }
        return Optional.ofNullable(incidencia);
    }
}
