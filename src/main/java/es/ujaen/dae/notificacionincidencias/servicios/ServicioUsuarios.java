package es.ujaen.dae.notificacionincidencias.servicios;

import es.ujaen.dae.notificacionincidencias.entidades.Direccion;
import es.ujaen.dae.notificacionincidencias.entidades.Usuario;
import es.ujaen.dae.notificacionincidencias.excepciones.UsuarioNoDisponible;
import es.ujaen.dae.notificacionincidencias.excepciones.UsuarioYaRegistrado;
import es.ujaen.dae.notificacionincidencias.repositorios.RepositorioUsuarios;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

/**
 * @author gcg00035
 */
@Service
@Validated
public class ServicioUsuarios {
    @Autowired
    RepositorioUsuarios usuariosRegistrados;

    public ServicioUsuarios() {

    }

    public void registrarUsuario(@Valid Usuario nuevoUsuario) {

        Optional<Usuario> existente = usuariosRegistrados.buscar(nuevoUsuario.email());
        if (existente.isPresent()) {
            throw new UsuarioYaRegistrado();
        }

        usuariosRegistrados.guardar(nuevoUsuario);

    }

    public Optional<Usuario> login(String email, String clave) {
        Optional<Usuario> usuarioOpt = usuariosRegistrados.buscar(email);
        if (usuarioOpt.isEmpty()) {
            return Optional.empty();
        }

        Usuario usuario = usuarioOpt.get();

        if (usuario.hashClave().equals(clave)) {
            return Optional.of(usuario);
        }

        return Optional.empty();
    }

    @Transactional
    public void cambiarClave(String email, String claveAntigua, String claveNueva) {
        Optional<Usuario> usuario = usuariosRegistrados.buscar(email);

        if (usuario.isEmpty()) {
            throw new UsuarioNoDisponible();
        }

        if (!usuario.get().hashClave().equals(claveAntigua)) {
            throw new IllegalArgumentException("Contraseña antigua incorrecta");
        }

        usuario.get().cambiarClave(claveNueva);
    }

    public void actualizarPerfil(@Valid Direccion dir, String email) {

    Optional<Usuario> usuario = usuariosRegistrados.buscar(email);

    if (usuario.isEmpty()) {

        throw new UsuarioNoDisponible();
    }

    usuario.get().direccion(dir);

    }

}
