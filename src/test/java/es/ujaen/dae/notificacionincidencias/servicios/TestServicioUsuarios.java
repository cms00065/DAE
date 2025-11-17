package es.ujaen.dae.notificacionincidencias.servicios;

import es.ujaen.dae.notificacionincidencias.entidades.*;
import es.ujaen.dae.notificacionincidencias.excepciones.UsuarioNoDisponible;
import es.ujaen.dae.notificacionincidencias.excepciones.UsuarioYaRegistrado;
import es.ujaen.dae.notificacionincidencias.servicios.ServicioUsuarios;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

/**
 * @author gcg00035
 */

@SpringBootTest(classes = es.ujaen.dae.notificacionincidencias.app.NotificacionIncidencias.class)
@ActiveProfiles("test")
public class TestServicioUsuarios {

    @Autowired
    ServicioUsuarios servicio;

    @Test
    @DirtiesContext
    void testRegistrarUsuario() {
        //Test para comprobar que no se aceptar usuarios no válidos
        //Test con parámetros incorrectos (Teléfono y Correo
        var direccion = new Direccion("Calle Real", "15", "2ºA", "Jaén", "23001");
        var usuario = new Usuario("Javier", "Gómez", LocalDate.of(1995, 5, 12), direccion,
                "611205", "javig-gmail.com", "javig", "clave1234",   // hashClave (en tu caso sin encriptar)
                Rol.CIUDADANO);


        assertThatThrownBy(() -> servicio.registrarUsuario(usuario)).isInstanceOf(ConstraintViolationException.class);

        var usuario2 = new Usuario("Javier", "Gómez", LocalDate.of(1995, 5, 12), direccion,
                "611203025", "javig@gmail.com", "javig", "clave1234",   // hashClave (en tu caso sin encriptar)
                Rol.CIUDADANO);

        servicio.registrarUsuario(usuario2);
        assertThatThrownBy(() -> servicio.registrarUsuario(usuario2)).isInstanceOf(UsuarioYaRegistrado.class);



    }

    @Test
    @DirtiesContext
    void testLoginUsuario() {
        var direccion = new Direccion("Calle Real", "15", "2ºA", "Jaén", "23001");
        var usuario = new Usuario("Javier", "Gómez", LocalDate.of(1995, 5, 12), direccion,
                "611203025", "javig@gmail.com", "javig", "clave1234",   // hashClave (en tu caso sin encriptar)
                Rol.CIUDADANO);

        servicio.registrarUsuario(usuario);

        //Prueba de email incorrecto
        assertThat(servicio.login("prueba@gmail.com", "clave1234")).isEmpty();

        //Prueba de contraseña incorrecta
        assertThat(servicio.login("javig@gmail.com", "prueba")).isEmpty();

        //Login correcto
        assertThat(servicio.login("javig@gmail.com", "clave1234")).hasValueSatisfying(u -> u.email().equals(usuario.email()));

    }

    @Test
    @DirtiesContext
    void testCambiarClaveUsuario() {
        var direccion = new Direccion("Calle Real", "15", "2ºA", "Jaén", "23001");
        var usuario = new Usuario("Javier", "Gómez", LocalDate.of(1995, 5, 12), direccion,
                "611203025", "javig@gmail.com", "javig", "clave1234",   // hashClave (en tu caso sin encriptar)
                Rol.CIUDADANO);

        servicio.registrarUsuario(usuario);

        //Login antes del cambio de contraseña
        assertThat(servicio.login("javig@gmail.com", "clave1234")).hasValueSatisfying(u -> u.email().equals(usuario.email()));

        var usuarioBD = servicio.login("javig@gmail.com", "clave1234").get();
        //Creo un nuevo usuario

        // --- Crear usuarioNuevo con la nueva clave ---
        var usuarioNuevo = new Usuario();
        usuarioNuevo.nombre(usuario.nombre());
        usuarioNuevo.apellidos(usuario.apellidos());
        usuarioNuevo.direccion(usuario.direccion());
        usuarioNuevo.telefono(usuario.telefono());
        usuarioNuevo.email(usuario.email());
        usuarioNuevo.login(usuario.login());
        usuarioNuevo.rol(usuario.rol());

        usuarioNuevo.cambiarClave("claveNueva");


        // Hago la actualización en el servico
        servicio.actualizarPerfil(usuarioBD, usuarioNuevo);

        // Login con la contraseña antigua (debe fallar)
        assertThat(servicio.login("javig@gmail.com", "clave1234")).isEmpty();

        // Login con la nueva contraseña (debe funcionar)
        assertThat(servicio.login("javig@gmail.com", "claveNueva"))
                .hasValueSatisfying(u -> u.email().equals(usuario.email()));

        // Intento de que un usuario "hacker" cambie obtenga las credenciales de otro usuario
        var usuarioFalso = new Usuario();
        usuarioFalso.nombre("Intruso");
        usuarioFalso.apellidos("Hackerman");
        usuarioFalso.direccion(direccion);
        usuarioFalso.telefono("600000000");
        usuarioFalso.email("noexiste@gmail.com");
        usuarioFalso.login("intruso");
        usuarioFalso.cambiarClave("claveNueva");
        usuarioFalso.rol(Rol.CIUDADANO);

        assertThatThrownBy(() -> servicio.actualizarPerfil(usuarioFalso, usuarioNuevo))
                .isInstanceOf(SecurityException.class);
    }

}
