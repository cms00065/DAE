package es.ujaen.dae.notificacionincidencias.rest;

import es.ujaen.dae.notificacionincidencias.entidades.EstadoIncidencia;
import es.ujaen.dae.notificacionincidencias.entidades.Incidencia;
import es.ujaen.dae.notificacionincidencias.entidades.Usuario;
import es.ujaen.dae.notificacionincidencias.excepciones.IncidenciaNoDisponible;
import es.ujaen.dae.notificacionincidencias.excepciones.UsuarioNoDisponible;
import es.ujaen.dae.notificacionincidencias.excepciones.UsuarioNoEsAdmin;
import es.ujaen.dae.notificacionincidencias.excepciones.UsuarioNoEsCreador;
import es.ujaen.dae.notificacionincidencias.rest.dto.MapeadorIncidencia;
import es.ujaen.dae.notificacionincidencias.rest.dto.MapeadorUsuario;
import es.ujaen.dae.notificacionincidencias.rest.dto.dtoIncidencia;
import es.ujaen.dae.notificacionincidencias.rest.dto.dtoUsuario;
import es.ujaen.dae.notificacionincidencias.servicios.ServicioIncidencias;
import es.ujaen.dae.notificacionincidencias.servicios.ServicioUsuarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author jma00081
 */
@RestController
@RequestMapping("/incidencias")
public class ControladorIncidencias {
    @Autowired
    MapeadorIncidencia mapeadorIncidencia;

    @Autowired
    private MapeadorUsuario mapeadorUsuario;

    @Autowired
    ServicioIncidencias servicioIncidencia;

    @Autowired
    ServicioUsuarios servicioUsuario;

    @PostMapping("/registro")
    public ResponseEntity<Void> nuevaIncidencia(@RequestBody dtoIncidencia incidencia) {
        try {
            servicioIncidencia.crearIncidencia(mapeadorIncidencia.entidad(incidencia));
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (DataIntegrityViolationException e) {
            // Error común si faltan datos obligatorios o claves foráneas incorrectas
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            // Esto te ayudará a ver cualquier otra excepción en la consola mientras depuras
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<dtoIncidencia> obtenerIncidenciaPorId(@PathVariable int id) {
        try {
            Optional<Incidencia> optionalIncidencia = servicioIncidencia.buscarIncidencia(id);

            if (optionalIncidencia.isPresent()) {
                Incidencia incidencia = optionalIncidencia.get();
                dtoIncidencia dto = mapeadorIncidencia.dto(incidencia);
                return ResponseEntity.ok(dto);
            } else {
                throw new IncidenciaNoDisponible();
            }
        } catch (IncidenciaNoDisponible e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }

    @PostMapping("/{id}/actualizarEstado/{nuevoEstado}")
    public ResponseEntity<dtoIncidencia> actualizarEstado(@PathVariable int id,
                                                          @PathVariable EstadoIncidencia nuevoEstado,
                                                          @RequestBody dtoUsuario dUsuario) {
        try {
            Optional<Incidencia> optionalIncidencia = servicioIncidencia.buscarIncidencia(id);

            if (optionalIncidencia.isPresent()) {
                Incidencia incidencia = optionalIncidencia.get();
                Usuario usuario = mapeadorUsuario.entidad(dUsuario);
                servicioIncidencia.cambiarEstado(usuario, incidencia, nuevoEstado);
                dtoIncidencia dto = mapeadorIncidencia.dto(incidencia);
                return ResponseEntity.ok(dto);
            }
        } catch (IncidenciaNoDisponible e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UsuarioNoEsAdmin e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @PostMapping("/{id}/foto")
    public ResponseEntity<dtoIncidencia> anadirFoto(@PathVariable int id,
                                                    @RequestBody byte[] foto,
                                                    Authentication authentication) {
        try {
            Optional<Incidencia> optionalIncidencia = servicioIncidencia.buscarIncidencia(id);

            if (optionalIncidencia.isPresent()) {
                Incidencia incidencia = optionalIncidencia.get();
                String email = authentication.getName();
                Optional<Usuario> optionalUsuario = servicioUsuario.buscarUsuario(email);

                if (optionalUsuario.isEmpty()) {
                    throw new UsuarioNoDisponible();
                }

                Usuario usuario = optionalUsuario.get();
                servicioIncidencia.anadirFoto(usuario, incidencia, foto);
                dtoIncidencia dto = mapeadorIncidencia.dto(incidencia);
                return ResponseEntity.ok(dto);
            }
        } catch (IncidenciaNoDisponible | UsuarioNoDisponible e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UsuarioNoEsCreador e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
}
