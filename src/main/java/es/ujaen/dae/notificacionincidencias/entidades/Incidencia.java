package es.ujaen.dae.notificacionincidencias.entidades;

import jakarta.persistence.Embedded;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * @author jma00081
 */
public class Incidencia {
    @Positive
    int id;

    LocalDate fecha;
    LocalDate fechaUltimaActualizacion;

    @NotBlank (message = "La descripción no puede estar vacía")
    String descripcion;

    @Pattern(regexp = "^C/\\s.+$", message = "La localización debe empezar por 'C/ ' seguido del nombre de la calle")
    String localizacion;

    @Enumerated
    EstadoIncidencia estado;

    @NotNull
    CoordenadasGPS ubicacionGPS;

    @NotNull
    TipoIncidencia tipo;

    public Incidencia() {}

    public Incidencia(int id, LocalDate fecha, String descripcion, String localizacion, EstadoIncidencia estado, CoordenadasGPS ubicacionGPS, TipoIncidencia tipo) {
        this.id = id;
        this.fecha = fecha;
        this.fechaUltimaActualizacion = fecha;
        this.descripcion = descripcion;
        this.localizacion = localizacion;
        this.estado = estado;
        this.ubicacionGPS = ubicacionGPS;
        this.tipo = tipo;
    }

    /**
     * Obtiene el id de la incidencia
     * @return el id
     */
    public int id() {
        return id;
    }

    /**
     * Obtiene la fecha de la incidencia
     * @return la fecha
     */
    public LocalDate fecha() {
        return fecha;
    }

    /**
     * Obtiene la descripción de la incidencia
     * @return la descripción
     */
    public String descripcion() {
        return descripcion;
    }

    /**
     * Obtiene la localización de la incidencia
     * @return la localización
     */
    public String localizacion() {
        return localizacion;
    }

    /**
     * Obtiene la ubicación GPS de la incidencia
     * @return la ubicación GPS
     */
    public EstadoIncidencia estado() {
        return estado;
    }

    /**
     * Obtiene la ubicación GPS de la incidencia
     * @return la ubicación GPS
     */
    public LocalDate fechaUltimaActualizacion() {
        return fechaUltimaActualizacion;
    }

    /**
     * Obtiene la ubicación GPS de la incidencia
     * @return la ubicación GPS
     */
    public TipoIncidencia tipo() {
        return tipo;
    }

    /**
     * Establece el id de la incidencia
     * @param id
     */
    public void id(int id) {
        this.id = id;
    }

    /**
     * Establece la fecha de la incidencia
     * @param fecha
     */
    public void fecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    /**
     * Establece la fecha de la última actualización de la incidencia
     * @param fechaUltimaActualizacion
     */
    public void fechaUltimaActualizacion(LocalDate fechaUltimaActualizacion) {
        this.fechaUltimaActualizacion = fechaUltimaActualizacion;
    }

    /**
     * Establece la descripción de la incidencia
     * @param descripcion
     */
    public void descripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Establece la localización de la incidencia
     * @param localizacion
     */
    public void localizacion(String localizacion) {
        this.localizacion = localizacion;
    }

    /**
     * Establece el estado de la incidencia
     * @param estado
     */
    public void estado(EstadoIncidencia estado) {
        this.estado = estado;
    }

    /**
     * Establece la ubicación GPS de la incidencia
     * @param ubicacionGPS
     */
    public void ubicacionGPS(CoordenadasGPS ubicacionGPS) {
        this.ubicacionGPS = ubicacionGPS;
    }

    /**
     * Establece el tipo de la incidencia
     * @param tipo
     */
    public void tipo(TipoIncidencia tipo) {
        this.tipo = tipo;
    }

    /**
     * Indica si el usuario tiene permiso para borrar la incidencia
     * Solo los usuarios con rol ADMIN pueden borrar la incidencia
     * @param usuario Usuario que solicita el borrado
     * @return true si el usuario es ADMIN, false en caso contrario
     */
    public boolean puedeBorrar(Usuario usuario) {
        return usuario.rol() == Rol.ADMIN;
    }

    /**
     * Cambia el estado de la incidencia y actualiza la fecha de la última modificación
     * @param nuevoEstado Nuevo estado que se asignará a la incidencia
     */
    public void cambiarEstado(EstadoIncidencia nuevoEstado) {
        this.estado = nuevoEstado;
        this.fechaUltimaActualizacion = LocalDate.now();
    }
}

