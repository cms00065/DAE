package es.ujaen.dae.notificacionincidencias.rest;

import es.ujaen.dae.notificacionincidencias.entidades.Direccion;
import es.ujaen.dae.notificacionincidencias.entidades.Rol;
import es.ujaen.dae.notificacionincidencias.rest.dto.dtoAltaTipoIncidencia;
import es.ujaen.dae.notificacionincidencias.rest.dto.dtoAutenticacionUsuario;
import es.ujaen.dae.notificacionincidencias.rest.dto.dtoTipoIncidencia;
import es.ujaen.dae.notificacionincidencias.rest.dto.dtoUsuario;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = es.ujaen.dae.notificacionincidencias.app.NotificacionIncidencias.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = {"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TestControladorTipoIncidencias {

    @LocalServerPort
    int localPort;

    TestRestTemplate restTemplate;

    // Operación auxiliar para generar los headers de autenticación con token JWT
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

    String tokenAdmin;
    String tokenCiudadano;

    // ============================================================
    // ================= MÉTODOS AUXILIARES =======================
    // ============================================================

    /**
     * Crea un usuario ADMIN para poder realizar operaciones restringidas.
     */
    private void registrarUsuarioAdmin() {
        var usuario = new dtoUsuario(
                0, "Admin", "Root",
                LocalDate.of(1990, 1, 1),
                new Direccion("a", "1", "b", "Jaén", "23001"),
                "600000001",
                "admin@ujaen.es",
                "admin.login",
                "CLAVE",
                Rol.ADMIN
        );
        restTemplate.postForEntity("/usuarios", usuario, Void.class);
    }

    /**
     * Crea un usuario CIUDADANO normal.
     */
    private void registrarUsuarioCiudadano() {
        var usuario = new dtoUsuario(
                0, "User", "Normal",
                LocalDate.of(1990, 1, 1),
                new Direccion("a", "1", "b", "Jaén", "23001"),
                "600000002",
                "user@ujaen.es",
                "user.login",
                "CLAVE",
                Rol.CIUDADANO
        );
        restTemplate.postForEntity("/usuarios", usuario, Void.class);
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
     * 3) Autentico ambos y guardo sus tokens
     */
    @BeforeEach
    void prepararUsuariosYTokens() {
        registrarUsuarioAdmin();
        registrarUsuarioCiudadano();

        tokenAdmin = autenticar("admin@ujaen.es", "CLAVE");
        tokenCiudadano = autenticar("user@ujaen.es", "CLAVE");
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

    // ============================================================
    // ========================= TESTS ============================
    // ============================================================

    /**
     * Test 1: Alta realizada por usuario admin
     */
    @Test
    void testAltaTipoIncidenciaAdmin() {

        var dto = new dtoTipoIncidencia(
                0,
                "Iluminación",
                "Farola rota",
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

        var respuesta = restTemplate.exchange(
                RequestEntity
                        .post("/tiposincidencia/alta")
                        .headers(headerAutorizacion(tokenAdmin))
                        .body(dtoAlta),
                Void.class
        );

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }


    /**
     * Test 2: Alta realizada por un usuario que no es admin
     */
    @Test
    void testAltaTipoIncidenciaNoAdmin() {

        var dto = new dtoTipoIncidencia(
                0,
                "Alcantarillado",
                "Tapa rota",
                true,
                null
        );

        var dtoAlta = new dtoAltaTipoIncidencia(
                new dtoUsuario(0, "", "", LocalDate.now(),
                        new Direccion("", "", "", "", ""),
                        "", "user@ujaen.es", "user.login",
                        "CLAVE", Rol.CIUDADANO),
                dto
        );

        var respuesta = restTemplate.exchange(
                RequestEntity
                        .post("/tiposincidencia/alta")
                        .headers(headerAutorizacion(tokenCiudadano))
                        .body(dtoAlta),
                Void.class
        );

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }


    /**
     * Test 3: Alta duplicada
     */
    @Test
    void testAltaDuplicada() {
        // Primero damos de alta uno correcto
        var alta = nuevaAlta("Basuras", "Contenedor quemado", tokenAdmin);
        assertThat(alta.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Segundo: mismo nombre → CONFLICT
        alta = nuevaAlta("Basuras", "Contenedor quemado", tokenAdmin);
        assertThat(alta.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }


    /**
     * Test 4: Listar todos los tipos de incidencias activos
     */
    @Test
    void testListarActivos() {

        nuevaAlta("Arbolado", "Rama caída", tokenAdmin);
        nuevaAlta("Movilidad", "Paso de peatones borrado", tokenAdmin);

        var respuesta = restTemplate.exchange(
                RequestEntity
                        .get("/tiposincidencia/activos")
                        .headers(headerAutorizacion(tokenAdmin))
                        .build(),
                dtoTipoIncidencia[].class
        );

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).hasSize(2);
    }


    /**
     * Test 5: Buscar por ID existente
     */
    @Test
    void testBuscarPorIdExistente() {
        // Crear uno
        ResponseEntity<Void> alta = nuevaAlta("Aceras", "Baldosa suelta", tokenAdmin);
        assertThat(alta.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Buscar ID = 1 (primero creado en BDD vacía de test)
        var respuesta = restTemplate.exchange(
                RequestEntity
                        .get("/tiposincidencia/1")
                        .headers(headerAutorizacion(tokenAdmin))
                        .build(),
                dtoTipoIncidencia.class
        );

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody().nombre()).isEqualTo("Aceras");
    }


    /**
     * Test 6: Buscar por ID que no existe
     */
    @Test
    void testBuscarPorIdInexistente() {

        var respuesta = restTemplate.exchange(
                RequestEntity
                        .get("/tiposincidencia/999")
                        .headers(headerAutorizacion(tokenAdmin))
                        .build(),
                Void.class
        );

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


    /**
     * Test 7: Baja realizada por usuario que es admin
     */
    @Test
    void testBajaAdmin() {

        nuevaAlta("Ruido", "Discoteca ilegal", tokenAdmin);

        var dtoUsuarioAdmin = new dtoUsuario(
                0, "", "", LocalDate.now(),
                new Direccion("", "", "", "", ""),
                "", "admin@ujaen.es", "admin.login",
                "CLAVE", Rol.ADMIN
        );

        var respuesta = restTemplate.exchange(
                RequestEntity
                        .post("/tiposincidencia/baja/1")
                        .headers(headerAutorizacion(tokenAdmin))
                        .body(dtoUsuarioAdmin),
                String.class
        );

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
    }


    /**
     * Test 8: Baja realizada por usuario que no es admin
     */
    @Test
    void testBajaNoAdmin() {

        nuevaAlta("Vehículos", "Coche abandonado", tokenAdmin);

        var dtoNOAdmin = new dtoUsuario(
                0, "", "", LocalDate.now(),
                new Direccion("", "", "", "", ""),
                "", "user@ujaen.es", "user.login",
                "CLAVE", Rol.CIUDADANO
        );

        var respuesta = restTemplate.exchange(
                RequestEntity
                        .post("/tiposincidencia/baja/1")
                        .headers(headerAutorizacion(tokenCiudadano))
                        .body(dtoNOAdmin),
                Void.class
        );

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    // ============================================================
    // ======= TEST 9: Baja de tipo inexistente → 404 NOT FOUND ===
    // ============================================================

    /**
     * Test 9: Baja de un tipo de incidencia que no existe
     */
    @Test
    void testBajaTipoNoExiste() {

        var dtoAdmin = new dtoUsuario(
                0, "", "", LocalDate.now(),
                new Direccion("", "", "", "", ""),
                "", "admin@ujaen.es", "admin.login",
                "CLAVE", Rol.ADMIN
        );

        var respuesta = restTemplate.exchange(
                RequestEntity
                        .post("/tiposincidencia/baja/999")
                        .headers(headerAutorizacion(tokenAdmin))
                        .body(dtoAdmin),
                Void.class
        );

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
