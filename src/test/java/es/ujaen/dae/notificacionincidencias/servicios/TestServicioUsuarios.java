package es.ujaen.dae.notificacionincidencias.servicios;

import es.ujaen.dae.notificacionincidencias.entidades.*;
import es.ujaen.dae.notificacionincidencias.excepciones.UsuarioYaRegistrado;
import es.ujaen.dae.notificacionincidencias.servicios.ServicioUsuarios;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;

/**
 * @author gcg00035
 */

@SpringBootTest(classes = es.ujaen.dae.notificacionincidencias.app.NotificacionIncidencias.class)
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

        servicio.cambiarClave("javig@gmail.com","clave1234","claveNueva");

        //Prueba de contraseña antigua
        assertThat(servicio.login("javig@gmail.com", "clave1234")).isEmpty();

        //Prueba con la contraseña nueva
        assertThat(servicio.login("javig@gmail.com", "claveNueva")).hasValueSatisfying(u -> u.email().equals(usuario.email()));
    }

}
