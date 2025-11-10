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

    /**
     * Actualiza los datos del usuario autenticado, incluyendo dirección, teléfono o clave.
     *
     * @param usuarioAntiguo usuario actualmente autenticado (ya persistido)
     * @param usuarioNuevo datos nuevos que el usuario desea aplicar
     */
    @Transactional
    public void actualizarPerfil(Usuario usuarioAntiguo, Usuario usuarioNuevo) {

        // Evitar que un usuario modifique otro
        if (usuarioAntiguo.id() != usuarioNuevo.id()) {
            throw new SecurityException("No se puede modificar otro usuario");
        }

        // Actualización de datos básicos (solo si no son nulos)
        if (usuarioNuevo.nombre() != null && !usuarioNuevo.nombre().isBlank()) {
            usuarioAntiguo.nombre(usuarioNuevo.nombre());
        }

        if (usuarioNuevo.apellidos() != null) {
            usuarioAntiguo.apellidos(usuarioNuevo.apellidos());
        }

        if (usuarioNuevo.direccion() != null) {
            usuarioAntiguo.direccion(usuarioNuevo.direccion());
        }

        if (usuarioNuevo.telefono() != null) {
            usuarioAntiguo.telefono(usuarioNuevo.telefono());
        }

        if (usuarioNuevo.fechaNacimiento() != null) {
            usuarioAntiguo.fechaNacimiento(usuarioNuevo.fechaNacimiento());
        }

        // ⚙️ Cambio de contraseña, si el usuario ha proporcionado una nueva
        if (usuarioNuevo.hashClave() != null && !usuarioNuevo.hashClave().isBlank()) {

            // Compruebo que la contraseña antigua coincide
            if (!usuarioAntiguo.verificarClave(usuarioNuevo.hashClave())) {
                throw new IllegalArgumentException("Contraseña antigua incorrecta");
            }

            usuarioAntiguo.cambiarClave(usuarioNuevo.hashClave());
        }
    }

}
