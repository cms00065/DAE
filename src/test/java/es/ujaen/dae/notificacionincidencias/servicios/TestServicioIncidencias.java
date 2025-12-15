package es.ujaen.dae.notificacionincidencias.servicios;

import es.ujaen.dae.notificacionincidencias.entidades.*;
import es.ujaen.dae.notificacionincidencias.excepciones.*;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

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

        // 1. OBTENER LA INCIDENCIA GESTIONADA/PERSISTIDA antes de modificar
        Optional<Incidencia> optIncidenciaGuardada = servicioIncidencias.buscarIncidencia(incidencia.id());
        Incidencia incidenciaPersistida = optIncidenciaGuardada.orElseThrow();

        // 2. Ejecutar el cambio de estado con la entidad obtenida de la BBDD
        servicioIncidencias.cambiarEstado(admin, incidenciaPersistida, EstadoIncidencia.RESUELTA);

        // 3. BUSCAR DE NUEVO para verificar que el cambio se ha persistido correctamente.
        Incidencia resultado = servicioIncidencias.buscarIncidencia(incidenciaPersistida.id()).orElseThrow();

        assertThat(resultado.estado()).isEqualTo(EstadoIncidencia.RESUELTA);
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

    /**
     * Buscar incidencias cercanas (<=10m) con estados válidos
     */
    @Test
    @DirtiesContext
    void testIncidenciasMenorIgualDiezMetros(){
        // ---------- Datos básicos ----------
        Direccion dir = new Direccion("C/ Real", "1", "2A", "Jaén", "23001");
        var admin = new Usuario("Admin", "Uno", null, dir, "600000001", "admin@ujaen.es", "admin", "clave", Rol.ADMIN);
        var usuario = new Usuario("Pedro", "Cliente", null, dir, "600000002", "pedro@ujaen.es", "pedro", "clave", Rol.CIUDADANO);

        servicioUsuarios.registrarUsuario(admin);
        servicioUsuarios.registrarUsuario(usuario);

        var tipo = new TipoIncidencia(0, "Alumbrado", "Farola fundida", true, null);
        servicioTipoIncidencia.alta(admin, tipo);

        // ---------- Incidencia base ----------
        var base = new Incidencia(
                LocalDate.now(),
                "Incidencia base",
                "C/ Uno",
                EstadoIncidencia.PENDIENTE,
                new CoordenadasGPS(37.000000, -3.000000),
                tipo,
                usuario
        );
        servicioIncidencias.crearIncidencia(base);

        // ---------- Incidencia cercana (< 10 m) ----------
        var cercana = new Incidencia(
                LocalDate.now(),
                "Cerca de base",
                "C/ Dos",
                EstadoIncidencia.PENDIENTE,
                new CoordenadasGPS(37.000050, -3.000050), // Diferencia muy pequeña
                tipo,
                usuario
        );
        servicioIncidencias.crearIncidencia(cercana);

        // ---------- TEST ----------
        var resultado = servicioIncidencias.buscarIncidenciasCercanasPendientesOEnTramite(new CoordenadasGPS(37.000000, -3.000000));

        assertThat(resultado)
                .extracting(Incidencia::descripcion)
                .contains("Cerca de base");
    }

    /**
     * Incidencia fuera de rango (>10m) no debe aparecer
     */
    @Test
    @DirtiesContext
    void testIncidenciasCercanasFueraDeDiezMetros() {

        Direccion dir = new Direccion("C/ Real", "1", "2A", "Jaén", "23001");
        var admin = new Usuario("Admin", "Uno", null, dir, "600000001", "admin@ujaen.es", "admin", "clave", Rol.ADMIN);
        var usuario = new Usuario("Lucia", "Cliente", null, dir, "600000003", "lucia@ujaen.es", "lucia", "clave", Rol.CIUDADANO);

        servicioUsuarios.registrarUsuario(admin);
        servicioUsuarios.registrarUsuario(usuario);

        var tipo = new TipoIncidencia(0, "Basuras", "Contenedor", true, null);
        servicioTipoIncidencia.alta(admin, tipo);

        //Incidencia base
        var base = new Incidencia(
                LocalDate.now(),
                "Base",
                "C/ Principal",
                EstadoIncidencia.PENDIENTE,
                new CoordenadasGPS(37.000000, -3.000000),
                tipo,
                usuario
        );
        servicioIncidencias.crearIncidencia(base);

        // A unos 100 metros aprox.
        var lejana = new Incidencia(
                LocalDate.now(),
                "Lejana",
                "C/ Secundaria",
                EstadoIncidencia.PENDIENTE,
                new CoordenadasGPS(37.000900, -3.000900),
                tipo,
                usuario
        );
        servicioIncidencias.crearIncidencia(lejana);

        var resultado = servicioIncidencias.buscarIncidenciasCercanasPendientesOEnTramite(new CoordenadasGPS(37.000000, -3.000000));

        assertThat(resultado)
                .extracting(Incidencia::descripcion)
                .doesNotContain("Lejana");
    }

    /**
     * Incidencias cercanas pero en estado no válido (RESUELTA)
     */
    @Test
    @DirtiesContext
    void testIncidenciasCercanasEstadoNoValido() {

        Direccion dir = new Direccion("C/ Real", "1", "2A", "Jaén", "23001");
        var admin = new Usuario("Admin", "Uno", null, dir, "600000001", "admin@ujaen.es", "admin", "clave", Rol.ADMIN);
        var usuario = new Usuario("Maria", "User", null, dir, "600000004", "maria@ujaen.es", "maria", "clave", Rol.CIUDADANO);

        servicioUsuarios.registrarUsuario(admin);
        servicioUsuarios.registrarUsuario(usuario);

        var tipo = new TipoIncidencia(0, "Obras", "Zanja", true, null);
        servicioTipoIncidencia.alta(admin, tipo);

        var base = new Incidencia(
                LocalDate.now(),
                "Base",
                "C/ Uno",
                EstadoIncidencia.PENDIENTE,
                new CoordenadasGPS(37.002000, -3.002000),
                tipo,
                usuario
        );
        servicioIncidencias.crearIncidencia(base);

        // Muy cerca, pero en estado RESUELTA debe ignorarse
        var resuelta = new Incidencia(
                LocalDate.now(),
                "Resuelta cerca",
                "C/ Dos",
                EstadoIncidencia.RESUELTA,
                new CoordenadasGPS(37.002005, -3.002005),
                tipo,
                usuario
        );
        servicioIncidencias.crearIncidencia(resuelta);

        var resultado = servicioIncidencias.buscarIncidenciasCercanasPendientesOEnTramite(new CoordenadasGPS(37.002000, -3.002000));

        assertThat(resultado)
                .extracting(Incidencia::descripcion)
                .doesNotContain("Resuelta cerca");
    }


    /**
     * Comprueba que, al intentar cambiar el estado de una incidencia concurrentemente,
     * uno de los hilos falla con IncidenciaConcurrente (debido al @Version).
     */
    @Test
    @DirtiesContext
    void testCambiarEstadoConcurrente() throws InterruptedException {
        // ---------- 1. PREPARACIÓN DE DATOS ----------
        Direccion direccion = new Direccion("Avenida de Madrid", "45", "7º B", "Jaén", "23007");
        var admin1 = new Usuario("Jose", "Mármol", null, direccion, "686547888", "admin1@ayto.es", "admin1", "claveJose", Rol.ADMIN);
        var admin2 = new Usuario("Ana", "García", null, direccion, "600123456", "admin2@ayto.es", "admin2", "claveAna", Rol.ADMIN);
        var tipo = new TipoIncidencia(0, "Alumbrado", "Farola rota", true, null);
        var coordenadas = new CoordenadasGPS(34.0522, -118.2437);
        var incidencia = new Incidencia(LocalDate.now(), "Farola rota", "C/ Mayor, nº 12", EstadoIncidencia.PENDIENTE, coordenadas, tipo, admin1);

        servicioUsuarios.registrarUsuario(admin1);
        servicioUsuarios.registrarUsuario(admin2);
        servicioTipoIncidencia.alta(admin1, tipo);
        servicioIncidencias.crearIncidencia(incidencia);

        final int incidenciaId = servicioIncidencias.buscarIncidenciasCreadasPor(admin1).getFirst().id();

        // Variables para capturar el resultado de los hilos
        AtomicBoolean hilo1Exito = new AtomicBoolean(false);
        AtomicBoolean hilo2Exito = new AtomicBoolean(false);

        // ---------- 2. OPERACIONES CONCURRENTES ----------

        // Hilo 1: Intenta cambiar a EN_EVALUACION
        Thread thread1 = new Thread(() -> {
            try {
                Incidencia i = servicioIncidencias.buscarIncidencia(incidenciaId).orElseThrow();
                servicioIncidencias.cambiarEstado(admin1, i, EstadoIncidencia.EN_EVALUACION);
                hilo1Exito.set(true);
            } catch (OptimisticLockingFailureException e) {
            } catch (Exception e) {
            }
        });

        // Hilo 2: Intenta cambiar a CERRADA
        Thread thread2 = new Thread(() -> {
            try {
                Incidencia i = servicioIncidencias.buscarIncidencia(incidenciaId).orElseThrow();
                servicioIncidencias.cambiarEstado(admin2, i, EstadoIncidencia.RESUELTA);
                hilo2Exito.set(true);
            } catch (OptimisticLockingFailureException e) {
            } catch (Exception e) {
            }
        });

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        // ---------- 3. ASUNCIONES (ASSERTIONS) ----------

        assertThat(hilo1Exito.get() ^ hilo2Exito.get())
                .isTrue();

        Incidencia resultadoFinal = servicioIncidencias.buscarIncidencia(incidenciaId).orElseThrow();

        assertThat(resultadoFinal.estado())
                .isIn(EstadoIncidencia.EN_EVALUACION, EstadoIncidencia.RESUELTA);
    }

    @Test
    @DirtiesContext
    void testBorrarIncidenciaConcurrente() throws InterruptedException {
        // ... PREPARACIÓN DE DATOS (Se mantiene igual) ...
        Direccion direccion = new Direccion("Avenida de Madrid", "45", "7º B", "Jaén", "23007");
        var admin1 = new Usuario("Jose", "Mármol", null, direccion, "686547888", "admin1@ayto.es", "admin1", "claveJose", Rol.ADMIN);
        var admin2 = new Usuario("Ana", "García", null, direccion, "600123456", "admin2@ayto.es", "admin2", "claveAna", Rol.ADMIN);
        var tipo = new TipoIncidencia(0, "Obras", "Obras sin señalizar", true, null);
        var coordenadas = new CoordenadasGPS(41.3851, 2.1734);
        var incidencia = new Incidencia(LocalDate.now(), "Obras sin señalizar", "C/ Av. Andalucía", EstadoIncidencia.PENDIENTE, coordenadas, tipo, admin1);

        servicioUsuarios.registrarUsuario(admin1);
        servicioUsuarios.registrarUsuario(admin2);
        servicioTipoIncidencia.alta(admin1, tipo);
        servicioIncidencias.crearIncidencia(incidencia);

        final int incidenciaId = servicioIncidencias.buscarIncidenciasCreadasPor(admin1).getFirst().id();

        // Para capturar si el hilo falló con la excepción IncidenciaNoDisponible
        AtomicReference<Exception> hilo1Exception = new AtomicReference<>();
        AtomicReference<Exception> hilo2Exception = new AtomicReference<>();

        // ... OPERACIONES CONCURRENTES (Se mantienen iguales) ...
        Thread thread1 = new Thread(() -> {
            try {
                Incidencia i = servicioIncidencias.buscarIncidencia(incidenciaId).orElseThrow();
                servicioIncidencias.borrar(admin1, i);
            } catch (Exception e) {
                hilo1Exception.set(e);
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                Incidencia i = servicioIncidencias.buscarIncidencia(incidenciaId).orElseThrow();
                servicioIncidencias.borrar(admin2, i);
            } catch (Exception e) {
                hilo2Exception.set(e);
            }
        });

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        // ---------- 3. ASUNCIONES (ASSERTIONS) ----------

        // 1. La incidencia DEBE haber sido eliminada
        assertThat(servicioIncidencias.buscarIncidencia(incidenciaId)).isEmpty();

        // 2. Solo uno de los hilos debe haber tenido éxito (excepción == null)
        boolean hilo1Exito = (hilo1Exception.get() == null);
        boolean hilo2Exito = (hilo2Exception.get() == null);

        assertThat(hilo1Exito ^ hilo2Exito)
                .isTrue();

        // 3. El hilo que falló, DEBE haber fallado por IncidenciaNoDisponible
        if (!hilo1Exito) {
            assertThat(hilo1Exception.get())
                    .isInstanceOf(IncidenciaNoDisponible.class);
        }
        if (!hilo2Exito) {
            assertThat(hilo2Exception.get())
                    .isInstanceOf(IncidenciaNoDisponible.class);
        }
    }
    /**
     * Comprueba el caso de concurrencia donde un hilo intenta cambiar el estado y
     * otro hilo intenta borrar la misma incidencia (Borrado vs. Update).
     * Uno debe tener éxito (el borrado), y el otro debe fallar (la actualización).
     */
    @Test
    @DirtiesContext
    void testBorradoVsCambioEstadoConcurrente() throws InterruptedException {
        // ---------- 1. PREPARACIÓN DE DATOS ----------
        Direccion direccion = new Direccion("Avenida de Madrid", "45", "7º B", "Jaén", "23007");
        var admin1 = new Usuario("Jose", "Mármol", null, direccion, "686547888", "admin1@ayto.es", "admin1", "claveJose", Rol.ADMIN);
        var admin2 = new Usuario("Ana", "García", null, direccion, "600123456", "admin2@ayto.es", "admin2", "claveAna", Rol.ADMIN);
        var tipo = new TipoIncidencia(0, "Obras", "Obras sin señalizar", true, null);
        var coordenadas = new CoordenadasGPS(41.3851, 2.1734);
        var incidencia = new Incidencia(LocalDate.now(), "Obras sin señalizar", "C/ Av. Andalucía", EstadoIncidencia.PENDIENTE, coordenadas, tipo, admin1);

        servicioUsuarios.registrarUsuario(admin1);
        servicioUsuarios.registrarUsuario(admin2);
        servicioTipoIncidencia.alta(admin1, tipo);
        servicioIncidencias.crearIncidencia(incidencia);

        final int incidenciaId = servicioIncidencias.buscarIncidenciasCreadasPor(admin1).getFirst().id();

        AtomicBoolean borradoExito = new AtomicBoolean(false);
        AtomicBoolean actualizacionExito = new AtomicBoolean(false);
        AtomicReference<Exception> actualizacionException = new AtomicReference<>();
        AtomicReference<Exception> borradoException = new AtomicReference<>();


        // ---------- 2. OPERACIONES CONCURRENTES ----------

        // Hilo 1 (BORRADO): Intenta eliminar la incidencia
        Thread threadBorrado = new Thread(() -> {
            try {
                Incidencia i = servicioIncidencias.buscarIncidencia(incidenciaId).orElseThrow();
                servicioIncidencias.borrar(admin1, i);
                borradoExito.set(true);
            } catch (Exception e) {
                borradoException.set(e);
            }
        });

        // Hilo 2 (ACTUALIZACIÓN): Intenta cambiar el estado a EN_EVALUACION
        Thread threadActualizacion = new Thread(() -> {
            try {
                Incidencia i = servicioIncidencias.buscarIncidencia(incidenciaId).orElseThrow();
                servicioIncidencias.cambiarEstado(admin2, i, EstadoIncidencia.EN_EVALUACION);
                actualizacionExito.set(true);
            } catch (OptimisticLockingFailureException e) {
                actualizacionException.set(e);
            } catch (IncidenciaNoDisponible e) {
                actualizacionException.set(e);
            } catch (Exception e) {
                actualizacionException.set(e);
            }
        });

        threadBorrado.start();
        threadActualizacion.start();

        threadBorrado.join();
        threadActualizacion.join();

        // ---------- 3. ASUNCIONES (ASSERTIONS) ----------

        // 1. La incidencia ya no debe existir, o si existe, solo debe haber una transacción exitosa
        Optional<Incidencia> resultadoFinal = servicioIncidencias.buscarIncidencia(incidenciaId);

        // 2. Se verifica que NO ambas transacciones tuvieron éxito
        assertThat(actualizacionExito.get() && borradoExito.get())
                .isFalse();

        if (resultadoFinal.isEmpty()) {
            assertThat(borradoExito.get())
                    .isTrue();

            assertThat(actualizacionExito.get())
                    .isFalse();

            assertThat(actualizacionException.get())
                    .isNotNull();
        } else {
            assertThat(actualizacionExito.get())
                    .isTrue();

            assertThat(resultadoFinal.get().estado())
                    .isEqualTo(EstadoIncidencia.EN_EVALUACION);

            assertThat(borradoException.get())
                    .isInstanceOfAny(IncidenciaNoDisponible.class, OptimisticLockingFailureException.class);
        }
    }
}
