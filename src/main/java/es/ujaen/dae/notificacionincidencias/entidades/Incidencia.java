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
    @Id
    @Positive
    int id;

    @DateTimeFormat
    LocalDate fecha;

    @NotBlank (message = "La descripción no puede estar vacía")
    String descripcion;

    @NotBlank (message = "La localización no puede estar vacía")
    String localizacion;

    @Enumerated
    EstadoIncidencia estado;

    @DateTimeFormat
    LocalDate fechaUltimaActualizacion;

    @Embedded
    CoordenadasGPS ubicacionGPS;

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

    public boolean puedeBorrar(Usuario usuario) {
        return usuario.rol() == Rol.ADMIN;
    }

    public void cambiarEstado(EstadoIncidencia nuevoEstado) {
        this.estado = nuevoEstado;
        this.fechaUltimaActualizacion = LocalDate.now();
    }
}
