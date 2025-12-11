package es.ujaen.dae.notificacionincidencias.rest.dto;

import es.ujaen.dae.notificacionincidencias.entidades.TipoIncidencia;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MapeadorTipoIncidencia {

    /**
     * @brief Utilizado para las operaciones de lectura
     * @param tipoIncidencia Entidad JPA obtenida desde la base de datos
     * @return DTO con los mismos datos
     */
    public dtoTipoIncidencia dto(TipoIncidencia tipoIncidencia) {
        return new dtoTipoIncidencia(tipoIncidencia.id(),
                tipoIncidencia.nombre(),
                tipoIncidencia.descripcion(),
                tipoIncidencia.isActivo(),
                tipoIncidencia.fechaAlta());
    }

    /**
     * @brief Convierte el DTO a la entidad TipoIncidencia ya existente
     * @details Utilizada para actualizar un registro ya existente (PUT o PATCH)
     * @param dTipoIncidencia DTO recibido del cliente
     * @return Entidad TipoIncidencia con los valores actualizados
     */
    public TipoIncidencia entidad(dtoTipoIncidencia dTipoIncidencia) {
        TipoIncidencia tipo = new TipoIncidencia(dTipoIncidencia.nombre(),
                dTipoIncidencia.descripcion(),
                dTipoIncidencia.activo(),
                dTipoIncidencia.fechaAlta()
        );

        tipo.id(dTipoIncidencia.id()); //Conservo el id generado automáticamente para actualizar
        return tipo;

    }

    /**
     * @brief Convierte un DTO a una nueva entidad TipoIncidencia
     * @details Utilizado para crear un nuevo tipo de incidencia (POST).
     * Ignoro el campo "id", ya que la base de datos lo genera automáticamente
     * @param dTipoIncidencia DTO recibido en la creación
     * @return Nueva entidad TipoIncidencia
     */
    public TipoIncidencia entidadNueva(dtoTipoIncidencia dTipoIncidencia){
        return new TipoIncidencia(
                dTipoIncidencia.nombre(),
                dTipoIncidencia.descripcion(),
                true, //La nueva incidencia va a estar activa
                LocalDateTime.now() //Fecha de alta actual
        );
    }

    /**
     * @brief Convierte la lista de entidades en una lista de DTOs
     * @details La utilizo para posibles respuestas GET que devuelven varios registros
     * @param entidades Lista de entidades TipoIncidencia obtenidas del servicio
     * @return Lista de DTOs equivalentes para enviar al cliente
     */
    public List<dtoTipoIncidencia> listaDto(List<TipoIncidencia> entidades){
        return entidades.stream().map(this::dto).toList();
    }
}
