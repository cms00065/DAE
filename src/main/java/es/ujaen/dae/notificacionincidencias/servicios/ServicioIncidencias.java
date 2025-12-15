package es.ujaen.dae.notificacionincidencias.servicios;

import es.ujaen.dae.notificacionincidencias.entidades.*;
import es.ujaen.dae.notificacionincidencias.excepciones.*;
import es.ujaen.dae.notificacionincidencias.repositorios.RepositorioIncidencia;
import es.ujaen.dae.notificacionincidencias.util.UtilGeodesia;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    //Radio máximo de búsqueda en metros para considerar incidencias cercanas
    private static final double radioProximidadMetros = 10.0;

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

        incidencia.foto(foto);
        incidencia.fechaUltimaActualizacion(LocalDate.now());

        repositorioIncidencias.actualizarFoto(incidencia);
    }

    /**
     * @brief Obtiene las incidencias que están en estado "PENDIENTE" o "EN_EVALUACION" y se encuentran como máximo a 10 metros de la ubicación indicada
     * @details Permite a la aplicación cliente comprobar si existen incidencias similares (cercanas y aún no resueltas) antes de registrar una nueva.
     * @param ubicacionReferencia Coordenadas GPS de la incidencia que se quiere comprobar
     * @return Lista de incidencias cercanas y no resueltas
     */
    public List<Incidencia> buscarIncidenciasCercanasPendientesOEnTramite(CoordenadasGPS ubicacionReferencia){
        List<Incidencia> incidencias = repositorioIncidencias.listarTodas();

        return incidencias.stream()
                .filter(i -> i.estado() == EstadoIncidencia.PENDIENTE
                        || i.estado() == EstadoIncidencia.EN_EVALUACION)
                .filter(i -> esCercana(ubicacionReferencia, i.ubicacionGPS()))
                .toList();
    }

    /**
     * @brief Comprueba si la distancia entre dos coordenadas es menor o igual al radio de proximidad configurado
     * @param referencia Coordenadas GPS de la incidencia que se quiere comprobar
     * @param destino Coordenadas de la incidencia destino
     * @return Devuelve true si la distancia es menor o igual al radio de proximidad configurado
     */
    private boolean esCercana(CoordenadasGPS referencia, CoordenadasGPS destino){
        double distancia = UtilGeodesia.distanciaMetros(referencia, destino);
        return distancia <= radioProximidadMetros;
    }
}
