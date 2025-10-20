package es.ujaen.dae.notificacionincidencias.servicios;

import es.ujaen.dae.notificacionincidencias.entidades.*;
import es.ujaen.dae.notificacionincidencias.excepciones.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author cms00065@red.ujaen.es
 */
@SpringBootTest(classes = es.ujaen.dae.notificacionincidencias.app.NotificacionIncidencias.class)
public class TestServicioTipoIncidencia {

    @Autowired
    ServicioTipoIncidencia servicio;

    /**
     * Comprueba que solo un usuario ADMIN puede dar de alta un tipo de incidencia.
     */
    @Test
    @DirtiesContext
    void testAltaSoloAdmin() {
        var admin = new Usuario("Admin", "Sistema", null, null, "600000000", "admin@ayto.es", "admin", "clave", Rol.ADMIN);
        var ciudadano = new Usuario("María", "López", null, null, "611223344", "maria@gmail.com", "maria", "1234", Rol.CIUDADANO);

        // Creamos un alta correcta por parte del administrador
        var resultado = servicio.alta(admin, new TipoIncidencia(1, "Suciedad", "Basura acumulada", false, null));
        assertThat(resultado.isActivo()).isTrue();
        assertThat(resultado.fechaAlta()).isNotNull();

        // Intento de alta por usuario normal conlleva una excepción
        assertThatThrownBy(() -> servicio.alta(ciudadano, new TipoIncidencia(2, "Rotura farola", "Farolas rotas", false, null)))
                .isInstanceOf(UsuarioNoEsAdmin.class);
    }

    /**
     * Comprueba que no se puede registrar un tipo con ID duplicado.
     */
    @Test
    @DirtiesContext
    void testAltaDuplicada() {
        var admin = new Usuario("Admin", "Sistema", null, null, "600000000", "admin@ayto.es", "admin", "clave", Rol.ADMIN);

        servicio.alta(admin, new TipoIncidencia(1, "Suciedad", "Basura acumulada", false, null));

        assertThatThrownBy(() -> servicio.alta(admin, new TipoIncidencia(1, "Rotura mobiliario", "Rotura de bancos", false, null)))
                .isInstanceOf(TipoIncidenciaYaExiste.class);
    }

    /**
     * Comprueba que no se puede dar de baja un tipo que esté siendo utilizado  por alguna incidencia activa.
     */
    @Test
    @DirtiesContext
    void testBajaTipoEnUso() {
        var admin = new Usuario("Admin", "Sistema", null, null, "600000000", "admin@ayto.es", "admin", "clave", Rol.ADMIN);

        var tipo = new TipoIncidencia(10, "Rotura", "Rotura de columpio", false, null);
        servicio.alta(admin, tipo);

        // Creamos una incidencia que usa ese tipo
        var incidencia = new Incidencia();
        incidencia.id(1);
        incidencia.descripcion("Columpio roto en parque infantil");
        incidencia.localizacion("Parque central");
        incidencia.estado(EstadoIncidencia.PENDIENTE);
        incidencia.tipo(tipo);

        List<Incidencia> incidencias = new ArrayList<>();
        incidencias.add(incidencia);

        assertThatThrownBy(() -> servicio.baja(admin, 10, incidencias))
                .isInstanceOf(TipoIncidenciaEstaEnUso.class);
    }

    /**
     * Comprueba que listarActivos devuelve solo los tipos activos.
     */
    @Test
    @DirtiesContext
    void testListarActivos() {
        var admin = new Usuario("Admin", "Sistema", null, null, "600000000", "admin@ayto.es", "admin", "clave", Rol.ADMIN);

        var tipo1 = new TipoIncidencia(1, "Suciedad", "Basura acumulada", false, null);
        var tipo2 = new TipoIncidencia(2, "Rotura mobiliario", "Rotura de bancos", false, null);

        servicio.alta(admin, tipo1);
        servicio.alta(admin, tipo2);

        // Marcamos el tipo 2 como inactivo
        servicio.baja(admin, 2, new ArrayList<>());

        List<TipoIncidencia> activos = servicio.listarActivos();

        assertThat(activos).hasSize(1);
        assertThat(activos.get(0).id()).isEqualTo(1);
        assertThat(activos.get(0).isActivo()).isTrue();
    }

    /**
     * Comprueba que buscarPorId devuelve el Optional adecuado.
     */
    @Test
    @DirtiesContext
    void testBuscarPorId() {
        var admin = new Usuario("Admin", "Sistema", null, null, "600000000", "admin@ayto.es", "admin", "clave", Rol.ADMIN);

        servicio.alta(admin, new TipoIncidencia(5, "Suciedad", "Basura acumulada", false, null));

        Optional<TipoIncidencia> resultadoExistente = servicio.buscarPorId(5);
        Optional<TipoIncidencia> resultadoInexistente = servicio.buscarPorId(999);

        assertThat(resultadoExistente).isPresent();
        assertThat(resultadoExistente.get().nombre()).isEqualTo("Suciedad");
        assertThat(resultadoInexistente).isEmpty();
    }
}
