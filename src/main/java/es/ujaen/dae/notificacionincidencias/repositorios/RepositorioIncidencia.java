package es.ujaen.dae.notificacionincidencias.repositorios;

import es.ujaen.dae.notificacionincidencias.entidades.Incidencia;
import es.ujaen.dae.notificacionincidencias.entidades.TipoIncidencia;
import es.ujaen.dae.notificacionincidencias.entidades.Usuario;
import es.ujaen.dae.notificacionincidencias.excepciones.IncidenciaYaCreada;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Entity;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

/**
 * @author jma00081
 */
@Repository
@Transactional
public class RepositorioIncidencia {
    @PersistenceContext
    EntityManager em;

    public Optional<Incidencia> buscarPorId(int id) {
        return Optional.ofNullable(em.find(Incidencia.class, id));
    }

    @Transactional(readOnly = true)
    public List<Incidencia> buscarPorFechaCreacion(LocalDate fecha) {
        return em.createQuery("SELECT i FROM Incidencia i WHERE i.fecha = :fechaBuscada", Incidencia.class)
                .setParameter("fechaBuscada", fecha)
                .getResultList();
    }

    @Transactional(readOnly = true)
    public List<Incidencia> buscarPorFechaUltimaModificacion(LocalDate fecha) {
        return em.createQuery("SELECT i FROM Incidencia i WHERE i.fecha = :fechaBuscada", Incidencia.class)
                .setParameter("fechaBuscada", fecha)
                .getResultList();
    }

    @Transactional(readOnly = true)
    public List<Incidencia> buscarPorTipo(TipoIncidencia tipo) {
        return em.createQuery("SELECT i FROM Incidencia i WHERE i.tipo = :tipoBuscado", Incidencia.class)
                .setParameter("tipoBuscado", tipo)
                .getResultList();

    }

    @Transactional(readOnly = true)
    public List<Incidencia> buscarIncidenciasCreadasPor(Usuario creador) {
        return em.createQuery("SELECT i FROM Incidencia i WHERE i.creador = :usuario", Incidencia.class)
                .setParameter("usuario", creador)
                .getResultList();
    }

    @Transactional
    public void eliminar(Incidencia incidencia) {
        em.remove(incidencia);
    }

    @Transactional
    public Incidencia actualizar(Incidencia incidencia) {
        return em.merge(incidencia);
    }

    @Transactional
    public void guardar(Incidencia incidencia) {
        if (em.find(Incidencia.class, id) != null) {
            throw new IncidenciaYaCreada();
        }

        em.persist(incidencia);
    }
}
