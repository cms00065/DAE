package es.ujaen.dae.notificacionincidencias.repositorios;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import es.ujaen.dae.notificacionincidencias.entidades.Usuario;
import es.ujaen.dae.notificacionincidencias.excepciones.UsuarioYaRegistrado;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author gcg00035
 */
@Repository
@Transactional
public class RepositorioUsuarios {
    @PersistenceContext
    EntityManager em;

    @Transactional(readOnly = true)
    public Optional<Usuario> buscar(String email) {
        TypedQuery<Usuario> q = em.createQuery(
                "SELECT u FROM Usuario u WHERE u.email = :email", Usuario.class);
        q.setParameter("email", email);
        try {
            Usuario resultado = q.getSingleResult();
            return Optional.of(resultado);
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public void guardar(Usuario usuario) {
        if (buscar(usuario.email()).isPresent()) {
            throw new UsuarioYaRegistrado();
        }

        em.persist(usuario);
    }

    public void actualizar(Usuario usuario) {
        em.merge(usuario);
    }


}
