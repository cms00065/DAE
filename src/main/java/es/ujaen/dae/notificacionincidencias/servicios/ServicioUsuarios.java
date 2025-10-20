package es.ujaen.dae.notificacionincidencias.servicios;

import es.ujaen.dae.notificacionincidencias.entidades.Direccion;
import es.ujaen.dae.notificacionincidencias.entidades.Usuario;
import es.ujaen.dae.notificacionincidencias.excepciones.UsuarioNoDisponible;
import es.ujaen.dae.notificacionincidencias.excepciones.UsuarioYaRegistrado;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author gcg00035
 */
@Service
@Validated
public class ServicioUsuarios {
    Map<String, Usuario> usuariosRegistrados;

    public ServicioUsuarios() {
        usuariosRegistrados = new HashMap<>();
    }

    public void registrarUsuario(@Valid Usuario nuevoUsuario) {

        if (usuariosRegistrados.containsKey(nuevoUsuario.email())) {
            throw new UsuarioYaRegistrado();
        }

        usuariosRegistrados.put(nuevoUsuario.email(), nuevoUsuario);

    }

    public Optional<Usuario> login(String email, String clave) {
        Usuario usuario = usuariosRegistrados.get(email);
        if (usuario == null) {
            return Optional.empty();
        }
        if (usuario.hashClave().equals(clave)) {
            return Optional.of(usuario);
        }
        return Optional.empty();
    }

    public void cambiarClave(String email, String claveAntigua, String claveNueva) {
        Usuario usuario = usuariosRegistrados.get(email);

        if (usuario != null && usuario.hashClave().equals(claveAntigua)) {
            usuario.cambiarClave(claveNueva);
        }
    }

    public void actualizarPerfil(@Valid Direccion dir, String email) {

    Usuario usuario = usuariosRegistrados.get(email);

    if (usuario == null) {

        throw new UsuarioNoDisponible();
    }

    usuario.direccion(dir);

    }

}
