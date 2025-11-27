package es.ujaen.dae.notificacionincidencias.rest;


import es.ujaen.dae.notificacionincidencias.entidades.Usuario;
import es.ujaen.dae.notificacionincidencias.excepciones.UsuarioYaRegistrado;
import es.ujaen.dae.notificacionincidencias.rest.dto.MapeadorUsuario;
import es.ujaen.dae.notificacionincidencias.rest.dto.dtoUsuario;
import es.ujaen.dae.notificacionincidencias.servicios.ServicioUsuarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * @author gcg00035
 */
@RestController
@RequestMapping("/usuarios")
public class ControladorUsuarios {

    @Autowired
    ServicioUsuarios servicioUsuarios;

    @Autowired
    MapeadorUsuario mapeadorUsuario;


    @PostMapping()
    public ResponseEntity<Void> nuevoUsuario(@RequestBody dtoUsuario usuario) {
        try {
            servicioUsuarios.registrarUsuario(mapeadorUsuario.entidadNueva(usuario));
        }
        catch(UsuarioYaRegistrado e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<dtoUsuario> login(@RequestBody dtoUsuario dUsuario) {
        // Asumimos que el campo 'login' del DTO es el email
        String email = dUsuario.login();
        String clave = dUsuario.clave();

        Optional<Usuario> usuarioOpt = servicioUsuarios.login(email, clave);

        if (usuarioOpt.isPresent()) {
            // Mapeamos la Entidad a DTO para la respuesta (oculta el hashClave)
            dtoUsuario respuesta = mapeadorUsuario.dto(usuarioOpt.get());
            return ResponseEntity.ok(respuesta); // 200 OK con los datos del usuario
        } else {
            // En caso de fallo (email no encontrado o clave incorrecta)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401 UNAUTHORIZED
        }
    }


}
