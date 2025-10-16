package es.ujaen.dae.notificacionincidencias.entidades;

import jakarta.persistence.Embedded;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

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


    public Incidencia(int id, LocalDate fecha, String descripcion, String localizacion, EstadoIncidencia estado,
                      LocalDate fechaUltimaActualizacion) {
        this.id = id;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.localizacion = localizacion;
        this.estado = estado;
        this.fechaUltimaActualizacion = fechaUltimaActualizacion;
    }


    public int id() {
        return id;
    }

    public LocalDate fecha() {
        return fecha;
    }

    public String descripcion() {
        return descripcion;
    }

    public String localizacion() {
        return localizacion;
    }

    public EstadoIncidencia estado() {
        return estado;
    }

    public LocalDate fechaUltimaActualizacion() {
        return fechaUltimaActualizacion;
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
