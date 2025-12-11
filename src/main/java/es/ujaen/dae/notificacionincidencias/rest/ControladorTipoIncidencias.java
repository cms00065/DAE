package es.ujaen.dae.notificacionincidencias.rest;

import es.ujaen.dae.notificacionincidencias.entidades.Incidencia;
import es.ujaen.dae.notificacionincidencias.entidades.TipoIncidencia;
import es.ujaen.dae.notificacionincidencias.entidades.Usuario;
import es.ujaen.dae.notificacionincidencias.excepciones.TipoIncidenciaEstaEnUso;
import es.ujaen.dae.notificacionincidencias.excepciones.TipoIncidenciaNoExiste;
import es.ujaen.dae.notificacionincidencias.excepciones.TipoIncidenciaYaExiste;
import es.ujaen.dae.notificacionincidencias.excepciones.UsuarioNoEsAdmin;
import es.ujaen.dae.notificacionincidencias.rest.dto.*;
import es.ujaen.dae.notificacionincidencias.servicios.ServicioIncidencias;
import es.ujaen.dae.notificacionincidencias.servicios.ServicioTipoIncidencia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author cms00065
 */

@RestController
@RequestMapping("/tiposincidencia")
public class ControladorTipoIncidencias {

    @Autowired
    ServicioTipoIncidencia servicioTipoIncidencia;

    @Autowired
    MapeadorTipoIncidencia mapeadorTipoIncidencia;

    @Autowired
    private MapeadorUsuario mapeadorUsuario;

    @Autowired
    private ServicioIncidencias servicioIncidencias;

    /**
     * @brief Registrar un nuevo tipo de incidencia en el sistema
     * @details Solo un usuario con rol ADMIN puede realizar la operación
     * @param datos DTO compuesto que contiene el usuario y el tipo de incidencia
     * @return código correspondiente según si alta correcta, si user no es admin o si ya existe el tipo de incidencia
     */
    @PostMapping("/alta")
    public ResponseEntity<?> crearTipoIncidencia(@RequestBody dtoAltaTipoIncidencia datos) {
        try{
            //Convierto el dtoUsuario a entidad con su mapeador
            Usuario usuario = mapeadorUsuario.entidad(datos.usuario());

            //Convierto el dtoTipoIncidencia a entidad con su mapeador
            TipoIncidencia tipo = mapeadorTipoIncidencia.entidadNueva(datos.tipo());

            servicioTipoIncidencia.alta(usuario, tipo); //Llamo al servicio

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (UsuarioNoEsAdmin e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }catch (TipoIncidenciaYaExiste e){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /**
     * @brief Devuelve la lista de tipos de incidencia que están activos
     * @return Código de éxito con la lista de tipos activos, o error si no hay ninguno
     */
    @GetMapping("/activos")
    public ResponseEntity<List<dtoTipoIncidencia>> listarTiposActivos(){
        List<TipoIncidencia> activos = servicioTipoIncidencia.listarActivos();

        if(activos.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        List<dtoTipoIncidencia> listaDtos = mapeadorTipoIncidencia.listaDto(activos);
        return ResponseEntity.ok(listaDtos);
    }

    /**
     * @brief Buscar un tipo de incidencia según su identificador
     * @param id Identificador del tipo de incidencia
     * @return OK con el DTO si existe, o error si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<dtoTipoIncidencia> buscarTipoPorId(@PathVariable int id){
        Optional<TipoIncidencia> tipoOpt = servicioTipoIncidencia.buscarPorId(id);

        if(tipoOpt.isPresent()){
            dtoTipoIncidencia dto = mapeadorTipoIncidencia.dto(tipoOpt.get());
            return ResponseEntity.ok(dto);
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * @brief Marcar un tipo de incidencia como inactivo (baja lógica)
     * @details Si el tipo de incidencia está en uso en alguna incidencia activa, no puede eliminarse
     * @param dUsuario Usuario autenticado que solicita la baja
     * @param id Identificador del tipo de incidencia a dar de baja
     * @return Código de éxito si se da de baja correctamente, o error si no es admin, si el tipo de incidencia está en uso o si no se encuentra
     */
    @PostMapping("/baja/{id}")
    public ResponseEntity<?> bajaTipoIncidencia(@RequestBody dtoUsuario dUsuario, @PathVariable int id){
        try{
            //Convierto el dto de usuario en la entidad de dominio
            Usuario usuario = mapeadorUsuario.entidad(dUsuario);

            //Obtengo la lista de incidencias
            List<Incidencia> incidencias = servicioIncidencias.listarTodas();

            //Se da de baja el tipo de incidencia por el usuario
            servicioTipoIncidencia.baja(usuario, id, incidencias);
            return ResponseEntity.ok("Tipo de incidencia desactivado correctamente");
        }catch (UsuarioNoEsAdmin e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }catch (TipoIncidenciaEstaEnUso e){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }catch (TipoIncidenciaNoExiste e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


}
