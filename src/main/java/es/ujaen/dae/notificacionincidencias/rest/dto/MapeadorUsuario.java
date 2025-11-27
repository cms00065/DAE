package es.ujaen.dae.notificacionincidencias.rest.dto;

import es.ujaen.dae.notificacionincidencias.entidades.Direccion;
import es.ujaen.dae.notificacionincidencias.entidades.Usuario;
import org.springframework.stereotype.Service;

@Service
public class MapeadorUsuario {

    /**
     * @brief Función utilizada para operaciones de lectura
     * @param usuario
     * @return
     */
    public dtoUsuario dto(Usuario usuario) {

        return new dtoUsuario(
                usuario.id(),
                usuario.nombre(),
                usuario.apellidos(),
                usuario.fechaNacimiento(),
                usuario.direccion(),
                usuario.telefono(),
                usuario.email(),
                usuario.login(),
                "", //Clave vacía en la respuesta
                usuario.rol());
    }

    /**
     * @brief DTO -> Entidad para ACTUALIZACIÓN (PUT/PATCH).
     * @param dUsuario DTO de entrada.
     */
    public Usuario entidad(dtoUsuario dUsuario) {
        return new Usuario(
                dUsuario.nombre(),
                dUsuario.apellidos(),
                dUsuario.fechaNacimiento(),
                dUsuario.direccion(),
                dUsuario.telefono(),
                dUsuario.email(),
                dUsuario.login(),
                dUsuario.clave(),
                dUsuario.rol()
        );
    }

    /**
     * @brief DTO -> Entidad para CREACIÓN (POST).
     * @param dUsuario DTO de entrada.
     */
    public Usuario entidadNueva(dtoUsuario dUsuario) {
        return new Usuario(
                dUsuario.nombre(),
                dUsuario.apellidos(),
                dUsuario.fechaNacimiento(),
                dUsuario.direccion(),
                dUsuario.telefono(),
                dUsuario.email(),
                dUsuario.login(),
                dUsuario.clave(),
                dUsuario.rol()
        );
    }

}
