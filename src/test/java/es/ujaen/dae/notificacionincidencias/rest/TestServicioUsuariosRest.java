package es.ujaen.dae.notificacionincidencias.rest;


import es.ujaen.dae.notificacionincidencias.entidades.Direccion;
import es.ujaen.dae.notificacionincidencias.entidades.Rol;
import es.ujaen.dae.notificacionincidencias.rest.dto.dtoUsuario;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;


/**
 * @author gcg00035
 */
@SpringBootTest(classes = es.ujaen.dae.notificacionincidencias.app.NotificacionIncidencias.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = {"test"})
public class TestServicioUsuariosRest {

    @LocalServerPort
    int localPort;

    TestRestTemplate restTemplate;

    static HttpHeaders headerAutorizacion(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        return headers;
    }

    /**
     * Crear un TestRestTemplate para las pruebas
     */
    @PostConstruct
    void crearRestTemplateBuilder() {
        var restTemplateBuilder = new RestTemplateBuilder()
                .rootUri("http://localhost:" + localPort );

        restTemplate = new TestRestTemplate(restTemplateBuilder);
    }

    /**
     * Intento de creación de un cliente inválido
     */
    @Test
    public void testNuevoClienteInvalido() {
        Direccion direccionFicticia = new Direccion("C/ Falsa", "1", "3A", "Jaén", "23001");
        Rol rolFicticio = Rol.CIUDADANO;
        LocalDate fechaNacimiento = LocalDate.of(1990, 1, 1);
        String loginFicticio = "pedro.jaen";
        int idFicticio = 0; // ID arbitrario para una creación

        // 1. Test con teléfono e email inválidos (611225 y pjaengmail.com)
        var usuario = new dtoUsuario(
                idFicticio,
                "Pedro",
                "Jaén Jaén",
                fechaNacimiento,
                direccionFicticia,
                "611225",           // <-- Teléfono inválido
                "pjaengmail.com",   // <-- Email inválido
                loginFicticio,
                "miClAvE",
                rolFicticio
        );
        var respuesta = restTemplate.postForEntity(
                "/usuarios",
                usuario,
                Void.class
        );
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        // 2. Usuario válido
        var usuario2 = new dtoUsuario(
                idFicticio,
                "Pedro",
                "Jaén Jaén",
                fechaNacimiento,
                direccionFicticia,
                "611301114",        // <-- Teléfono válido (9 dígitos)
                "pjaen@gmail.com",  // <-- Email válido
                loginFicticio,
                "miClAvE",
                rolFicticio
        );
        respuesta = restTemplate.postForEntity(
                "/usuarios",
                usuario2,
                Void.class
        );
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // 3. Intento de crear el mismo usuario (conflicto)
        respuesta = restTemplate.postForEntity(
                "/usuarios",
                usuario2,
                Void.class
        );
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }



}
