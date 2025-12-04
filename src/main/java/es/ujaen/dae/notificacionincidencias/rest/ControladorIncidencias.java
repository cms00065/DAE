package es.ujaen.dae.notificacionincidencias.rest;

import es.ujaen.dae.notificacionincidencias.entidades.EstadoIncidencia;
import es.ujaen.dae.notificacionincidencias.entidades.Incidencia;
import es.ujaen.dae.notificacionincidencias.entidades.TipoIncidencia;
import es.ujaen.dae.notificacionincidencias.entidades.Usuario;
import es.ujaen.dae.notificacionincidencias.excepciones.IncidenciaNoDisponible;
import es.ujaen.dae.notificacionincidencias.excepciones.UsuarioNoEsAdmin;
import es.ujaen.dae.notificacionincidencias.rest.dto.MapeadorIncidencia;
import es.ujaen.dae.notificacionincidencias.rest.dto.MapeadorUsuario;
import es.ujaen.dae.notificacionincidencias.rest.dto.dtoIncidencia;
import es.ujaen.dae.notificacionincidencias.rest.dto.dtoUsuario;
import es.ujaen.dae.notificacionincidencias.servicios.ServicioIncidencias;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author jma00081
 */
@RestController
@RequestMapping("/incidencias")
public class ControladorIncidencias {
    @Autowired
    MapeadorIncidencia mapeador;

    @Autowired
    ServicioIncidencias servicio;
    @Autowired
    private MapeadorUsuario mapeadorUsuario;

    @PostMapping()
    public ResponseEntity<Void> nuevaIncidencia(@RequestBody dtoIncidencia incidencia) {
        servicio.crearIncidencia(mapeador.entidad(incidencia));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/incidencias/{id}")
    public ResponseEntity<dtoIncidencia> obtenerIncidenciaPorId(@PathVariable int id) {
        try {
            Optional<Incidencia> optionalIncidencia = servicio.buscarIncidencia(id);

            if (optionalIncidencia.isPresent()) {
                Incidencia incidencia = optionalIncidencia.get();
                dtoIncidencia dto = mapeador.dto(incidencia);
                return ResponseEntity.ok(dto);
            } else {
                throw new IncidenciaNoDisponible();
            }
        } catch (IncidenciaNoDisponible e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }

    @PostMapping("/incidencias/{id}/actualizarEstado")
    public ResponseEntity<dtoIncidencia> actualizarEstado(@PathVariable int id,
                                                          @RequestBody EstadoIncidencia nuevoEstado,
                                                          @RequestBody dtoUsuario dUsuario) {
        try {
            Optional<Incidencia> optionalIncidencia = servicio.buscarIncidencia(id);

            if (optionalIncidencia.isPresent()) {
                Incidencia incidencia = optionalIncidencia.get();
                Usuario usuario = mapeadorUsuario.entidad(dUsuario);
                servicio.cambiarEstado(usuario, incidencia, nuevoEstado);
                dtoIncidencia dto = mapeador.dto(incidencia);
                return ResponseEntity.ok(dto);
            } else {
                throw new IncidenciaNoDisponible();
            }
        } catch (IncidenciaNoDisponible e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UsuarioNoEsAdmin e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

    }
}
