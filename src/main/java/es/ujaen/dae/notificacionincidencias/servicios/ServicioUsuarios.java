package es.ujaen.dae.notificacionincidencias.servicios;

import es.ujaen.dae.notificacionincidencias.entidades.Usuario;
import es.ujaen.dae.notificacionincidencias.excepciones.UsuarioYaRegistrado;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author gcg00035
 */
@Service
public class ServicioUsuarios {
    Map<String, Usuario> usuariosRegistrados;

    public ServicioUsuarios() {
        usuariosRegistrados = new HashMap<String, Usuario>();
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

    public void cambiarClave(String email, String clave) {
        Usuario usuario = usuariosRegistrados.get(email);

        if (usuario != null) {
            usuario.cambiarClave(clave);
        }
    }

    //TODO actualizarPerfil

}
