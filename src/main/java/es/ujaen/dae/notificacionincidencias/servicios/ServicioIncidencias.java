package es.ujaen.dae.notificacionincidencias.servicios;

import es.ujaen.dae.notificacionincidencias.entidades.*;
import es.ujaen.dae.notificacionincidencias.excepciones.*;
import es.ujaen.dae.notificacionincidencias.repositorios.RepositorioIncidencia;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
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

    public void crearIncidencia(@Valid Incidencia nuevaIncidencia) {
        repositorioIncidencias.guardar(nuevaIncidencia);
    }

    public Optional<Incidencia> buscarIncidencia(int id) {
        return repositorioIncidencias.buscarPorId(id);
    }

    public List<Incidencia> buscarIncidenciasCreadasPor(Usuario actor) {
        return repositorioIncidencias.buscarIncidenciasCreadasPor(actor);
    }

    public List<Incidencia> buscarIncidenciasPorTipo(TipoIncidencia tipo) {
        return repositorioIncidencias.buscarPorTipo(tipo);
    }

    public List<Incidencia> buscarIncidenciasPorEstado(EstadoIncidencia estado) {
        return repositorioIncidencias.buscarPorEstado(estado);
    }

    public void borrar(Usuario actor, Incidencia incidencia) {
        if (incidencia.puedeBorrar(actor)) {
            repositorioIncidencias.eliminar(incidencia);
        }
    }

    public List<Incidencia> listarTodas(){
        return repositorioIncidencias.listarTodas();
    }

    public void cambiarEstado(Usuario actor, Incidencia incidencia, EstadoIncidencia nuevoEstado) {
        if (actor.rol() != Rol.ADMIN) {
            throw new UsuarioNoEsAdmin();
        }

        Optional<Incidencia> incidenciaExistente = repositorioIncidencias.buscarPorId(incidencia.id());
        if (incidenciaExistente.isEmpty()) {
            throw new IncidenciaNoDisponible();
        }

        incidencia.cambiarEstado(nuevoEstado);
        repositorioIncidencias.actualizarEstado(incidencia);
    }

    public void anadirFoto(Usuario actor, Incidencia incidencia, byte[] foto) {
        Optional<Incidencia> incidenciaExistente = repositorioIncidencias.buscarPorId(incidencia.id());
        if (incidenciaExistente.isEmpty()) {
            throw new IncidenciaNoDisponible();
        }

        if (incidencia.creador().id() != actor.id() && actor.rol() != Rol.ADMIN) {
            throw new UsuarioNoEsCreador();
        }

        incidencia.setFoto(foto);
        incidencia.fechaUltimaActualizacion(LocalDate.now());

        repositorioIncidencias.actualizarFoto(incidencia);
    }
}
