package es.ujaen.dae.notificacionincidencias.repositorios;

import es.ujaen.dae.notificacionincidencias.entidades.EstadoIncidencia;
import es.ujaen.dae.notificacionincidencias.entidades.Incidencia;
import es.ujaen.dae.notificacionincidencias.entidades.TipoIncidencia;
import es.ujaen.dae.notificacionincidencias.entidades.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author jma00081
 */
@Repository
@Transactional
public class RepositorioIncidencia {
    @PersistenceContext
    EntityManager em;

    @Transactional(readOnly = true)
    public Optional<Incidencia> buscarPorId(int id) {
        return Optional.ofNullable(em.find(Incidencia.class, id));
    }

    @Transactional(readOnly = true)
    public List<Incidencia> buscarPorTipo(TipoIncidencia tipo) {
        return em.createQuery("SELECT i FROM Incidencia i WHERE i.tipo = :tipoBuscado", Incidencia.class)
                .setParameter("tipoBuscado", tipo)
                .getResultList();

    }

    @Transactional(readOnly = true)
    public List<Incidencia> buscarPorEstado(EstadoIncidencia estado) {
        return em.createQuery("SELECT i FROM Incidencia i WHERE i.estado = :estadoBuscado", Incidencia.class)
                .setParameter("estadoBuscado", estado)
                .getResultList();
    }

    @Transactional(readOnly = true)
    public List<Incidencia> buscarIncidenciasCreadasPor(Usuario creador) {
        return em.createQuery("SELECT i FROM Incidencia i WHERE i.creador = :usuario", Incidencia.class)
                .setParameter("usuario", creador)
                .getResultList();
    }
    
    @Transactional(readOnly = true)
    public List<Incidencia> listarTodas(){
        return em.createQuery("SELECT i FROM Incidencia i", Incidencia.class).getResultList();
    }

    public void eliminar(Incidencia incidencia) {
        Incidencia gestionada = em.merge(incidencia);
        em.remove(gestionada);
    }

    public void actualizarEstado(Incidencia incidencia) {
        em.merge(incidencia);
    }

    public void guardar(Incidencia incidencia) {
        em.persist(incidencia);
    }
}
