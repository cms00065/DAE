package es.ujaen.dae.notificacionincidencias.rest;

import es.ujaen.dae.notificacionincidencias.entidades.Direccion;
import es.ujaen.dae.notificacionincidencias.entidades.Rol;
import es.ujaen.dae.notificacionincidencias.rest.dto.dtoAutenticacionUsuario;
import es.ujaen.dae.notificacionincidencias.rest.dto.dtoUsuario;
import es.ujaen.dae.notificacionincidencias.util.UtilJwt;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = es.ujaen.dae.notificacionincidencias.app.NotificacionIncidencias.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = {"test"})
public class TestControladorToken {
    @LocalServerPort
    int localPort;

    TestRestTemplate restTemplate;

    /**
     * Crear un TestRestTemplate para las pruebas
     */
    @PostConstruct
    void crearRestTemplateBuilder() {
        var restTemplateBuilder = new RestTemplateBuilder()
                .rootUri("http://localhost:" + localPort);

        restTemplate = new TestRestTemplate(restTemplateBuilder);
    }

    @Test
    void testAutenticacionToken(){
        //Registro el usuario
        Direccion direccion = new Direccion("C/Real", "1", "2A", "Úbeda", "23400");
        var usuario = new dtoUsuario(0, "Antonio", "Martínez López",
                LocalDate.of(1990, 1, 1), direccion, "611223344",
                "antonio@ujaen.es", "antonio.login", "CLAVESEGURA", Rol.ADMIN);

        var respuestaRegistro = restTemplate.postForEntity(
                "/usuarios",
                    usuario,
                    Void.class
        );
        assertThat(respuestaRegistro.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        //Si el email es inexistente, debe devolver código de error
        var loginInvalido1 = new dtoAutenticacionUsuario("noexiste@ujaen.es", "CLAVESEGURA");

        var respuesta1 = restTemplate.postForEntity(
                "/auth/autenticacion",
                loginInvalido1,
                String.class
        );
        assertThat(respuesta1.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        //Si la clave es incorrecta, debe devolver código de error
        var loginInvalido2 = new dtoAutenticacionUsuario("antonio@ujaen.es", "CLAVEINCORRECTA");
        var respuesta2 = restTemplate.postForEntity(
                "/auth/autenticacion",
                loginInvalido2,
                String.class
        );
        assertThat(respuesta2.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        //Por último, compruebo que si el login correcto devuelve código de éxito y token generado
        var loginCorrecto = new dtoAutenticacionUsuario( "antonio@ujaen.es", "CLAVESEGURA");
        var respuestaCorrecta = restTemplate.postForEntity(
                "/auth/autenticacion",
                loginCorrecto,
                String.class
        );
        assertThat(respuestaCorrecta.getStatusCode()).isEqualTo(HttpStatus.OK);
        String token = respuestaCorrecta.getBody();
        assertThat(token).isNotNull().isNotEmpty();

        //Por último, valido el contenido del token JWT
        var claims = UtilJwt.extraerContenido(token);

        assertThat(claims.getSubject()).isEqualTo("antonio@ujaen.es");
        assertThat(claims.get("roles", String.class)).contains("ADMIN");
    }

}
