package es.ujaen.dae.notificacionincidencias.servicios;

import es.ujaen.dae.notificacionincidencias.entidades.*;
import es.ujaen.dae.notificacionincidencias.excepciones.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author jma00081
 */
@SpringBootTest(classes = es.ujaen.dae.notificacionincidencias.app.NotificacionIncidencias.class)
public class TestServicioIncidencias {

    @Autowired
    ServicioIncidencias servicioIncidencias;

    /**
     * Comprueba que un usuario logueado puede crear incidencias correctamente.
     */
    @Test
    @DirtiesContext
    void testCrearIncidenciaCorrecta() {
        var usuario = new Usuario("Ana", "García", null, null, "600123456", "ana@correo.es", "ana", "claveAna", Rol.CIUDADANO);
        var tipo = new TipoIncidencia(1, "Alumbrado", "Farola rota", true, null);
        var coordenadas = new CoordenadasGPS(34.0522, -118.2437);
        var incidencia = new Incidencia(100, LocalDate.now(), "Farola rota", "C/ Mayor, nº 12", EstadoIncidencia.PENDIENTE, coordenadas, tipo, usuario);

        servicioIncidencias.crearIncidencia(usuario, incidencia);

        var resultado = servicioIncidencias.listarMisInicidencias(usuario);
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).descripcion()).contains("Farola rota");
    }

    /**
     * Comprueba que un usuario no logueado no puede crear incidencias.
     */
    @Test
    @DirtiesContext
    void testCrearIncidenciaSinLogin() {
        var usuario = new Usuario("Luis", "Romero", null, null, "120723356", "luis@correo.es", null, "claveLuis", Rol.CIUDADANO);
        var tipo = new TipoIncidencia(2, "Basura", "Contenedor lleno", true, null);
        var coordenadas = new CoordenadasGPS(40.4168, -3.7038);
        var incidencia = new Incidencia(101, LocalDate.now(), "Contenedor lleno", "C/ Plaza del Sol", EstadoIncidencia.PENDIENTE, coordenadas, tipo, usuario);

        assertThatThrownBy(() -> servicioIncidencias.crearIncidencia(usuario, incidencia))
                .isInstanceOf(UsuarioNoLogueado.class);
    }

    /**
     * Comprueba que un usuario ADMIN puede buscar incidencias por tipo y estado.
     */
    @Test
    @DirtiesContext
    void testBuscarPorTipoYEstado() {
        var admin = new Usuario("Pedro", "del Moral", null, null, "600000000", "pedro@correo.es", "admin", "clavePedro", Rol.ADMIN);
        var tipo = new TipoIncidencia(3, "Vandalismo", "Pintadas en muro", true, null);
        var coordenadas = new CoordenadasGPS(37.3891, -5.9845);
        var incidencia = new Incidencia(102, LocalDate.now(), "Pintadas en muro del colegio", "C/ del Arroyo", EstadoIncidencia.PENDIENTE, coordenadas, tipo, admin);

        servicioIncidencias.crearIncidencia(admin, incidencia);

        var resultado = servicioIncidencias.buscar(admin, 3, EstadoIncidencia.PENDIENTE);
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).localizacion()).contains("Arroyo");
    }

    /**
     * Comprueba que un usuario no ADMIN no puede usar el método buscar.
     */
    @Test
    @DirtiesContext
    void testBuscarNoAdmin() {
        var usuario = new Usuario("Carlos", "Ruiz", null, null, "600555555", "carlos@correo.es", "carlos", "clave", Rol.CIUDADANO);

        assertThatThrownBy(() -> servicioIncidencias.buscar(usuario, 0, null))
                .isInstanceOf(UsuarioNoEsAdmin.class);
    }

    /**
     * Comprueba que se puede cambiar el estado de una incidencia si el usuario es ADMIN.
     */
    @Test
    @DirtiesContext
    void testCambiarEstadoIncidencia() {
        var admin = new Usuario("Jose", "Mármol", null, null, "686547888", "admin@ayto.es", "admin", "claveJose", Rol.ADMIN);
        var tipo = new TipoIncidencia(4, "Ruido", "Ruidos nocturnos", true, null);
        var coordenadas = new CoordenadasGPS(39.4699, -0.3763);
        var incidencia = new Incidencia(103, LocalDate.now(), "Ruidos en callejón", "C/ del Carril", EstadoIncidencia.PENDIENTE, coordenadas, tipo, admin);

        servicioIncidencias.crearIncidencia(admin, incidencia);

        Optional<Incidencia> modificada = servicioIncidencias.cambiarEstado(admin, 103, EstadoIncidencia.RESUELTA);
        assertThat(modificada).isPresent();
        assertThat(modificada.get().estado()).isEqualTo(EstadoIncidencia.RESUELTA);
    }

    /**
     * Comprueba que un usuario puede borrar su propia incidencia.
     */
    @Test
    @DirtiesContext
    void testBorrarIncidenciaPropia() {
        var usuario = new Usuario("Lucía", "Sánchez", null, null, "600111222", "lucia@correo.es", "lucia", "clave", Rol.CIUDADANO);
        var tipo = new TipoIncidencia(5, "Obras", "Obras sin señalizar", true, null);
        var coordenadas = new CoordenadasGPS(41.3851, 2.1734);
        var incidencia = new Incidencia(104, LocalDate.now(), "Obras sin señalizar en acera", "C/ Av. Andalucía", EstadoIncidencia.PENDIENTE, coordenadas, tipo, usuario);

        servicioIncidencias.crearIncidencia(usuario, incidencia);
        servicioIncidencias.borrar(usuario, 104);

        var resultado = servicioIncidencias.listarMisInicidencias(usuario);
        assertThat(resultado).isEmpty();
    }
}
