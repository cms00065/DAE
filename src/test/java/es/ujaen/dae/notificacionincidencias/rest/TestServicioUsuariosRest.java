package es.ujaen.dae.notificacionincidencias.rest;


import es.ujaen.dae.notificacionincidencias.entidades.Direccion;
import es.ujaen.dae.notificacionincidencias.entidades.Rol;
import es.ujaen.dae.notificacionincidencias.entidades.Usuario;
import es.ujaen.dae.notificacionincidencias.rest.dto.dtoAutenticacionUsuario;
import es.ujaen.dae.notificacionincidencias.rest.dto.dtoUsuario;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.web.bind.annotation.GetMapping;

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
                .rootUri("http://localhost:" + localPort);

        restTemplate = new TestRestTemplate(restTemplateBuilder);
    }

    /**
     * Intento de creación de un cliente inválido y válido
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

    /**
     * Test de obtención de clientes mediante email
     */
    @Test
    void testObtenerUsuarioPorEmail_CasosExitoYFallo() {
        String EMAIL_EXISTENTE = "ciudadano@ujaen.es",
                CLAVE = "miClave",
                EMAIL_INEXISTENTE = "noexiste@ujaen.es";
        Direccion direccionFicticia = new Direccion("C/ Falsa", "1", "3A", "Jaén", "23001");
        var usuario = new dtoUsuario(
                0,
                "Pedro",
                "Jaén Jaén",
                LocalDate.of(1990, 1, 1),
                direccionFicticia,
                "611301114",
                EMAIL_EXISTENTE,
                "pedro.jaen",
                CLAVE,
                Rol.ADMIN
        );
        ResponseEntity<Void> postResponse = restTemplate.postForEntity(
                "/usuarios",
                usuario,
                Void.class
        );

        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);


        // =======================================================
        // OBTENER EL TOKEN JWT DEL ADMIN
        // =======================================================
        dtoAutenticacionUsuario credencialesAdmin = new dtoAutenticacionUsuario(EMAIL_EXISTENTE, CLAVE);

        ResponseEntity<String> tokenResponse = restTemplate.postForEntity(
                "/auth/autenticacion",
                credencialesAdmin,
                String.class // Esperamos el String del JWT
        );

        assertThat(tokenResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        String jwtToken = tokenResponse.getBody();

        // 4. PREPARAR LAS CABECERAS CON EL TOKEN
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // =======================================================
        // A. CASO DE ÉXITO: USUARIO ENCONTRADO (200 OK)
        // =======================================================
        String urlExistente = "/usuarios/" + EMAIL_EXISTENTE;

        ResponseEntity<dtoUsuario> respuestaExistente = restTemplate.exchange(
                urlExistente,
                HttpMethod.GET,
                entity,
                dtoUsuario.class
        );

        // Aserciones de ÉXITO
        assertThat(respuestaExistente.getStatusCode())
                .as("Verificar código 200 OK para usuario existente")
                .isEqualTo(HttpStatus.OK);

        assertThat(respuestaExistente.getBody())
                .as("Verificar que el cuerpo de la respuesta no es nulo")
                .isNotNull();

        assertThat(respuestaExistente.getBody().email())
                .as("Verificar que el email del DTO coincide")
                .isEqualTo(EMAIL_EXISTENTE);


        // =======================================================
        // B. CASO DE FALLO: USUARIO NO ENCONTRADO (404 NOT FOUND)
        // =======================================================
        String urlInexistente = "/usuarios/" + EMAIL_INEXISTENTE;

        ResponseEntity<Void> respuestaInexistente = restTemplate.exchange(
                urlInexistente,
                HttpMethod.GET,
                entity,
                Void.class
        );

        // Aserciones de FALLO
        assertThat(respuestaInexistente.getStatusCode())
                .as("Verificar código 404 NOT FOUND para usuario inexistente")
                .isEqualTo(HttpStatus.NOT_FOUND);

        assertThat(respuestaInexistente.getBody())
                .as("Verificar que el cuerpo de la respuesta es nulo en caso de 404")
                .isNull();
    }
}
