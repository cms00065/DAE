package es.ujaen.dae.notificacionincidencias.rest;

import es.ujaen.dae.notificacionincidencias.entidades.CoordenadasGPS;
import es.ujaen.dae.notificacionincidencias.entidades.Direccion;
import es.ujaen.dae.notificacionincidencias.entidades.EstadoIncidencia;
import es.ujaen.dae.notificacionincidencias.entidades.Rol;
import es.ujaen.dae.notificacionincidencias.rest.dto.*;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = es.ujaen.dae.notificacionincidencias.app.NotificacionIncidencias.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = {"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TestControladorIncidencias {

    @LocalServerPort
    int localPort;

    TestRestTemplate restTemplate;

    dtoUsuario usuarioAdmin;
    dtoUsuario usuarioCiudadano;
    dtoTipoIncidencia tipoIncidencia;

    String tokenAdmin;
    String tokenCiudadano;

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
     * Crea un usuario ADMIN para poder realizar operaciones restringidas.
     */
    private void registrarUsuarioAdmin() {
        usuarioAdmin = new dtoUsuario(
                0, "Admin", "Root",
                LocalDate.of(1990, 1, 1),
                new Direccion("a", "1", "b", "Jaén", "23001"),
                "600000001",
                "admin@ujaen.es",
                "admin.login",
                "CLAVE",
                Rol.ADMIN
        );
        var resp = restTemplate.postForEntity("/usuarios", usuarioAdmin, Void.class);
        assertThat(resp.getStatusCode()).as("Fallo al crear Admin").isEqualTo(HttpStatus.CREATED);
    }

    /**
     * Crea un usuario CIUDADANO normal.
     */
    private void registrarUsuarioCiudadano() {
        usuarioCiudadano = new dtoUsuario(
                0, "User", "Normal",
                LocalDate.of(1990, 1, 1),
                new Direccion("a", "1", "b", "Jaén", "23001"),
                "600000002",
                "user@ujaen.es",
                "user.login",
                "CLAVE",
                Rol.CIUDADANO
        );
        var resp = restTemplate.postForEntity("/usuarios", usuarioCiudadano, Void.class);
        assertThat(resp.getStatusCode()).as("Fallo al crear Admin").isEqualTo(HttpStatus.CREATED);
    }

    /**
     * Crea un tipo de incidencia inicial.
     */
    private void registrarTipoIncidencia() {
        tipoIncidencia = new dtoTipoIncidencia(
                0,
                "Iluminacion",
                "Farola rota",
                true,
                null
        );

        dtoAltaTipoIncidencia altaTipoIncidencia = new dtoAltaTipoIncidencia(usuarioAdmin, tipoIncidencia);

        ResponseEntity<Void> respuestaAlta = restTemplate.exchange(
                RequestEntity
                        .post("/tiposincidencia/alta")
                        .headers(headerAutorizacion(tokenAdmin))
                        .body(altaTipoIncidencia),
                Void.class
        );

        assertThat(respuestaAlta.getStatusCode()).as("Fallo al crear el tipo de incidencia").isEqualTo(HttpStatus.CREATED);
    }

    /**
     * Autentica un usuario y devuelve su token JWT.
     */
    private String autenticar(String email, String clave) {
        var login = new dtoAutenticacionUsuario(email, clave);
        var response = restTemplate.postForEntity("/auth/autenticacion", login, String.class);
        return response.getBody();
    }

    /**
     * Antes de cada test:
     * 1) Creo un usuario ADMIN
     * 2) Creo un usuario CIUDADANO
     * 3) Creo un tipo de incidencia
     * 4) Autentico el admin y el ciudadano y guardo sus tokens
     */
    @BeforeEach
    void prepararInformacion() {
        registrarUsuarioAdmin();
        registrarUsuarioCiudadano();

        String emailAdmin = usuarioAdmin.email();
        String emailCiudadano = usuarioCiudadano.email();
        String urlAdmin = "/usuarios/" + emailAdmin;
        String urlCiudadano = "/usuarios/" + emailCiudadano;

        tokenAdmin = autenticar("admin@ujaen.es", "CLAVE");
        tokenCiudadano = autenticar("user@ujaen.es", "CLAVE");

        // Recuperar los usuarios para obtener los IDs reales asignados en la BD
        HttpHeaders headersAdmin = headerAutorizacion(tokenAdmin);
        var respGetAdmin = restTemplate.exchange(
                urlAdmin,
                HttpMethod.GET,
                new HttpEntity<>(headersAdmin),
                dtoUsuario.class,
                usuarioAdmin.email()
        );
        assertThat(respGetAdmin.getStatusCode()).as("Fallo al recuperar Admin").isEqualTo(HttpStatus.OK);
        usuarioAdmin = respGetAdmin.getBody();

        // Recuperar Ciudadano (usando su propio token)
        HttpHeaders headersCiudadano = headerAutorizacion(tokenCiudadano);
        var respGetCiudadano = restTemplate.exchange(
                urlCiudadano,
                HttpMethod.GET,
                new HttpEntity<>(headersCiudadano),
                dtoUsuario.class,
                usuarioCiudadano.email()
        );
        assertThat(respGetCiudadano.getStatusCode()).as("Fallo al recuperar Ciudadano").isEqualTo(HttpStatus.OK);
        usuarioCiudadano = respGetCiudadano.getBody();

        registrarTipoIncidencia();

        // Recuperar el tipo de incidencia para obtener su ID real asignado en la BD
        var respGetTipos = restTemplate.exchange(
                "/tiposincidencia/activos",
                HttpMethod.GET,
                new HttpEntity<>(headerAutorizacion(tokenAdmin)),
                dtoTipoIncidencia[].class
        );

        assertThat(respGetTipos.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertNotNull(respGetTipos.getBody());

        // Buscar el tipo de incidencia creado y actualizarlo
        tipoIncidencia = java.util.Arrays.stream(respGetTipos.getBody())
                .filter(t -> t.nombre().equals("Iluminacion"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("¡No se ha encontrado el tipo de incidencia creado!"));
    }

    /**
     * Operación auxiliar para dar de alta un nuevo tipo de incidencia.
     */
    private ResponseEntity<Void> nuevaAlta(String nombre, String descripcion, String token) {

        var dto = new dtoTipoIncidencia(
                0,
                nombre,
                descripcion,
                true,
                null
        );

        var dtoAlta = new dtoAltaTipoIncidencia(
                new dtoUsuario(0, "", "", LocalDate.now(),
                        new Direccion("", "", "", "", ""),
                        "", "admin@ujaen.es", "admin.login",
                        "CLAVE", Rol.ADMIN),
                dto
        );

        return restTemplate.exchange(
                RequestEntity
                        .post("/tiposincidencia/alta")
                        .headers(headerAutorizacion(token))
                        .body(dtoAlta),
                Void.class
        );
    }

    /**
     * Genera coordenadas GPS aleatorias.
     * @return CoordenadasGPS con latitud y longitud aleatorias
     */
    private CoordenadasGPS generarCoordenadas() {
        Random random = new Random();
        return new CoordenadasGPS(random.nextDouble(), random.nextDouble());
    }

    /**
     * Test 1: Registrar una incidencia correctamente
     */
    @Test
    void testRegistrarIncidencia() {

        var dtoIncidencia = new dtoIncidencia(
                0,
                LocalDate.now(),
                LocalDate.now(),
                "Farola fundida",
                "C/ Mayor, 10",
                EstadoIncidencia.PENDIENTE,
                generarCoordenadas(),
                String.valueOf(tipoIncidencia.id()),
                String.valueOf(usuarioCiudadano.id()),
                null
        );

        var respuesta = restTemplate.exchange(
                RequestEntity
                        .post("/incidencias/registro")
                        .headers(headerAutorizacion(tokenCiudadano))
                        .body(dtoIncidencia),
                Void.class
        );

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }


    /**
     * Test 2: Encontrar incidencia por ID existente
     */
    @Test
    void testObtenerIncidenciaPorId() {

        var dtoIncidencia = new dtoIncidencia(
                0,
                LocalDate.now(),
                LocalDate.now(),
                "Farola fundida",
                "C/ Mayor, 10",
                EstadoIncidencia.PENDIENTE,
                generarCoordenadas(),
                String.valueOf(tipoIncidencia.id()),
                String.valueOf(usuarioCiudadano.id()),
                null
        );

        var respuestaAlta = restTemplate.exchange(
                RequestEntity
                        .post("/incidencias/registro")
                        .headers(headerAutorizacion(tokenCiudadano))
                        .body(dtoIncidencia),
                Void.class
        );

        assertThat(respuestaAlta.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Obtener la incidencia por ID = 1 (primera creada en BDD de test)
        var respuestaGet = restTemplate.exchange(
                RequestEntity
                        .get("/incidencias/1")
                        .headers(headerAutorizacion(tokenCiudadano))
                        .build(),
                dtoIncidencia.class
        );

        assertThat(respuestaGet.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuestaGet.getBody().descripcion()).isEqualTo("Farola fundida");
    }


    /**
     * Test 3: Cambiar el estado de una incidencia
     */
    @Test
    void testActualizarEstadoIncidencia() {

        var dtoIncidencia = new dtoIncidencia(
                0,
                LocalDate.now(),
                LocalDate.now(),
                "Farola fundida",
                "C/ Mayor, 10",
                EstadoIncidencia.PENDIENTE,
                generarCoordenadas(),
                String.valueOf(tipoIncidencia.id()),
                String.valueOf(usuarioCiudadano.id()),
                null
        );

        var respuestaAlta = restTemplate.exchange(
                RequestEntity
                        .post("/incidencias/registro")
                        .headers(headerAutorizacion(tokenCiudadano))
                        .body(dtoIncidencia),
                Void.class
        );

        assertThat(respuestaAlta.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        var respuestaCambioEstadoAdmin = restTemplate.exchange(
                RequestEntity
                        .post("/incidencias/1/actualizarEstado/" + EstadoIncidencia.EN_EVALUACION)
                        .headers(headerAutorizacion(tokenAdmin))
                        .body(usuarioAdmin),
                dtoIncidencia.class
        );

        var respuestaCambioEstadoFail = restTemplate.exchange(
                RequestEntity
                        .post("/incidencias/1/actualizarEstado/" + EstadoIncidencia.EN_EVALUACION)
                        .headers(headerAutorizacion(tokenCiudadano))
                        .body(usuarioCiudadano),
                dtoIncidencia.class
        );

        assertThat(respuestaCambioEstadoAdmin.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuestaCambioEstadoAdmin.getBody().estado()).isEqualTo(EstadoIncidencia.EN_EVALUACION);

        assertThat(respuestaCambioEstadoFail.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }


    /**
     * Test 4: Añadir una foto a una incidencia
     */
    @Test
    void testAnadirFoto() {

        var dtoIncidencia = new dtoIncidencia(
                0,
                LocalDate.now(),
                LocalDate.now(),
                "Farola fundida",
                "C/ Mayor, 10",
                EstadoIncidencia.PENDIENTE,
                generarCoordenadas(),
                String.valueOf(tipoIncidencia.id()),
                String.valueOf(usuarioCiudadano.id()),
                null
        );

        var respuestaAlta = restTemplate.exchange(
                RequestEntity
                        .post("/incidencias/registro")
                        .headers(headerAutorizacion(tokenCiudadano))
                        .body(dtoIncidencia),
                Void.class
        );

        assertThat(respuestaAlta.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        byte[] fotoSimulada = new byte[]{1, 2, 3, 4, 5, 60, 70, 80};

        var respuestaAnadirFotoCreador = restTemplate.exchange(
                RequestEntity
                        .post("/incidencias/1/foto")
                        .headers(headerAutorizacion(tokenCiudadano))
                        .body(fotoSimulada),
                dtoIncidencia.class
        );

        var respuestaAnadirFotoAdmin = restTemplate.exchange(
                RequestEntity
                        .post("/incidencias/1/foto")
                        .headers(headerAutorizacion(tokenAdmin))
                        .body(fotoSimulada),
                dtoIncidencia.class
        );

        assertThat(respuestaAnadirFotoCreador.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuestaAnadirFotoCreador.getBody().foto()).isEqualTo(fotoSimulada);

        assertThat(respuestaAnadirFotoAdmin.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuestaAnadirFotoAdmin.getBody().foto()).isEqualTo(fotoSimulada);
    }
}
