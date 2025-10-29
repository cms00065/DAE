package es.ujaen.dae.notificacionincidencias.repositorios;

import es.ujaen.dae.notificacionincidencias.entidades.TipoIncidencia;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

/**
 * @author cms00065
 */

@Repository
@Transactional
public class RepositorioTipoIncidencia {

    @PersistenceContext
    EntityManager em;

    public Optional<TipoIncidencia> buscar(int id){
        return Optional.ofNullable(em.find(TipoIncidencia.class, id));
    }

    public boolean buscarPorNombre(String nombre){
        Long count = em.createQuery("SELECT COUNT(t) FROM TipoIncidencia t WHERE LOWER(t.nombre) = LOWER(:nombre) ", Long.class).setParameter("nombre", nombre).getSingleResult();

        return count > 0;
    }

    public void crear(TipoIncidencia tipoIncidencia){
        em.persist(tipoIncidencia);
    }

    public void eliminar(TipoIncidencia tipoIncidencia){
        TipoIncidencia gestionada = em.contains(tipoIncidencia) ? tipoIncidencia : em.merge(tipoIncidencia);
        em.remove(gestionada);
    }

    public TipoIncidencia actualizar(TipoIncidencia tipoIncidencia){
        return em.merge(tipoIncidencia);
    }

    public List<TipoIncidencia> listarActivos(){
        return em.createQuery("SELECT t FROM TipoIncidencia t WHERE t.activo = true", TipoIncidencia.class).getResultList();
    }


}
