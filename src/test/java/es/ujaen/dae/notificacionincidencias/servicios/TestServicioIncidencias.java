package es.ujaen.dae.notificacionincidencias.servicios;

import es.ujaen.dae.notificacionincidencias.entidades.*;
import es.ujaen.dae.notificacionincidencias.excepciones.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author jma00081
 */
@SpringBootTest(classes = es.ujaen.dae.notificacionincidencias.app.NotificacionIncidencias.class)
@ActiveProfiles("test")
public class TestServicioIncidencias {

    @Autowired
    ServicioIncidencias servicioIncidencias;

    @Autowired
    ServicioUsuarios servicioUsuarios;

    @Autowired
    ServicioTipoIncidencia servicioTipoIncidencia;

    /**
     * Comprueba que un usuario puede crear incidencias correctamente.
     */
    @Test
    @DirtiesContext
    void testCrearIncidenciaCorrecta() {
        Direccion direccion = new Direccion("Avenida de Madrid",
                "45",
                "7º B",
                "Jaén",
                "23007");
        var admin = new Usuario("Jose", "Mármol", null, direccion, "686547888", "admin@ayto.es", "admin", "claveJose", Rol.ADMIN);
        var usuario = new Usuario("Ana", "García", null, direccion, "600123456", "ana@correo.es", "ana", "claveAna", Rol.CIUDADANO);
        var tipo = new TipoIncidencia(0, "Alumbrado", "Farola rota", true, null);
        var coordenadas = new CoordenadasGPS(34.0522, -118.2437);
        var incidencia = new Incidencia(LocalDate.now(), "Farola rota", "C/ Mayor, nº 12", EstadoIncidencia.PENDIENTE, coordenadas, tipo, usuario);

        servicioUsuarios.registrarUsuario(admin);
        servicioUsuarios.registrarUsuario(usuario);
        servicioTipoIncidencia.alta(admin, tipo);
        servicioIncidencias.crearIncidencia(incidencia);

        var resultado = servicioIncidencias.buscarIncidenciasCreadasPor(usuario);
        //assertThat(resultado).contains(incidencia);
        assertThat(resultado.getFirst().descripcion()).contains("Farola rota");
    }

    /**
     * Comprueba que un usuario puede buscar incidencias por tipo y estado.
     */
    @Test
    @DirtiesContext
    void testBuscarPorTipoYEstado() {
        Direccion direccion = new Direccion("Avenida de Madrid",
                "45",
                "7º B",
                "Jaén",
                "23007");
        var admin = new Usuario("Jose", "Mármol", null, direccion, "686547888", "admin@ayto.es", "admin", "claveJose", Rol.ADMIN);
        var usuario = new Usuario("Pedro", "del Moral", null, direccion, "600000000", "pedro@correo.es", "pedro", "clavePedro", Rol.CIUDADANO);
        var tipo = new TipoIncidencia(0, "Vandalismo", "Pintadas en muro", true, null);
        var coordenadas = new CoordenadasGPS(37.3891, -5.9845);
        var incidencia = new Incidencia(LocalDate.now(), "Pintadas en muro del colegio", "C/ del Arroyo", EstadoIncidencia.PENDIENTE, coordenadas, tipo, usuario);

        servicioUsuarios.registrarUsuario(admin);
        servicioUsuarios.registrarUsuario(usuario);
        servicioTipoIncidencia.alta(admin, tipo);
        servicioIncidencias.crearIncidencia(incidencia);

        var resultado1 = servicioIncidencias.buscarIncidenciasPorTipo(tipo);
        var resultado2 = servicioIncidencias.buscarIncidenciasPorEstado(EstadoIncidencia.PENDIENTE);
        assertThat(resultado1).hasSize(1);
        assertThat(resultado2.get(0).localizacion()).contains("Arroyo");
    }

    /**
     * Comprueba que un usuario no ADMIN no puede borrar una incidencia suya que no esté en estado PENDIENTE
     */
    @Test
    @DirtiesContext
    void testEliminarNoAdminNoPendiente() {
        Direccion direccion = new Direccion("Avenida de Madrid",
                "45",
                "7º B",
                "Jaén",
                "23007");
        var admin = new Usuario("Jose", "Mármol", null, direccion, "686547888", "admin@ayto.es", "admin", "claveJose", Rol.ADMIN);
        var usuario = new Usuario("Carlos", "Ruiz", null, direccion, "600555555", "carlos@correo.es", "carlos", "clave", Rol.CIUDADANO);
        var tipo = new TipoIncidencia(0, "Vandalismo", "Pintadas en muro", true, null);
        var coordenadas = new CoordenadasGPS(37.3891, -5.9845);
        var incidencia = new Incidencia(LocalDate.now(), "Pintadas en muro del colegio", "C/ del Arroyo", EstadoIncidencia.EN_EVALUACION, coordenadas, tipo, usuario);

        servicioUsuarios.registrarUsuario(admin);
        servicioUsuarios.registrarUsuario(usuario);
        servicioTipoIncidencia.alta(admin, tipo);
        servicioIncidencias.crearIncidencia(incidencia);
        servicioIncidencias.borrar(usuario, incidencia);

        var resultado = servicioIncidencias.buscarIncidenciasCreadasPor(usuario);
        assertThat(resultado.getFirst().descripcion()).contains("Pintadas en muro");
    }

    /**
     * Comprueba que se puede cambiar el estado de una incidencia si el usuario es ADMIN.
     */
    @Test
    @DirtiesContext
    void testCambiarEstadoIncidencia() {
        Direccion direccion = new Direccion("Avenida de Madrid",
                "45",
                "7º B",
                "Jaén",
                "23007");
        var admin = new Usuario("Jose", "Mármol", null, direccion, "686547888", "admin@ayto.es", "admin", "claveJose", Rol.ADMIN);
        var tipo = new TipoIncidencia(0, "Ruido", "Ruidos nocturnos", true, null);
        var coordenadas = new CoordenadasGPS(39.4699, -0.3763);
        var incidencia = new Incidencia(LocalDate.now(), "Ruidos en callejón", "C/ del Carril", EstadoIncidencia.PENDIENTE, coordenadas, tipo, admin);

        servicioUsuarios.registrarUsuario(admin);
        servicioTipoIncidencia.alta(admin, tipo);
        servicioIncidencias.crearIncidencia(incidencia);
        servicioIncidencias.cambiarEstado(admin, incidencia, EstadoIncidencia.RESUELTA);

        assertThat(incidencia.estado()).isEqualTo(EstadoIncidencia.RESUELTA);
    }

    /**
     * Comprueba que un usuario puede borrar su propia incidencia si está en estado PENDIENTE
     */
    @Test
    @DirtiesContext
    void testBorrarIncidenciaPropia() {
        Direccion direccion = new Direccion("Avenida de Madrid",
                "45",
                "7º B",
                "Jaén",
                "23007");
        var admin = new Usuario("Jose", "Mármol", null, direccion, "686547888", "admin@ayto.es", "admin", "claveJose", Rol.ADMIN);
        var usuario = new Usuario("Lucía", "Sánchez", null, direccion, "600111222", "lucia@correo.es", "lucia", "clave", Rol.CIUDADANO);
        var tipo = new TipoIncidencia(0, "Obras", "Obras sin señalizar", true, null);
        var coordenadas = new CoordenadasGPS(41.3851, 2.1734);
        var incidencia = new Incidencia(LocalDate.now(), "Obras sin señalizar en acera", "C/ Av. Andalucía", EstadoIncidencia.PENDIENTE, coordenadas, tipo, usuario);

        servicioUsuarios.registrarUsuario(admin);
        servicioUsuarios.registrarUsuario(usuario);
        servicioTipoIncidencia.alta(admin, tipo);
        servicioIncidencias.crearIncidencia(incidencia);
        servicioIncidencias.borrar(usuario, incidencia);

        var resultado = servicioIncidencias.buscarIncidenciasCreadasPor(usuario);
        assertThat(resultado).isEmpty();
    }
}
