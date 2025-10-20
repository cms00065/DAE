package es.ujaen.dae.notificacionincidencias.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;


public class TipoIncidencia {
    @Positive
    int id;

    @NotBlank
    String nombre;

    @NotBlank
    String descripcion;

    boolean activo;

    //Fecha en la que se dio de alta el tipo de incidencia correspondiente
    LocalDateTime fechaAlta;

    public TipoIncidencia() {

    }

    public TipoIncidencia(int id, String nombre, String descripcion, boolean activo, LocalDateTime fechaAlta) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
        this.fechaAlta = fechaAlta;
    }

    //Getters y setters
    public int id() {
        return id;
    }

    public void id(int id) {
        this.id = id;
    }

    public String nombre() {
        return nombre;
    }

    public void nombre(String nombre) {
        this.nombre = nombre;
    }

    public String descripcion() {
        return descripcion;
    }

    public void descripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void isActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime fechaAlta() {
        return fechaAlta;
    }

    public void fechaAlta(LocalDateTime fechaAlta) {
        this.fechaAlta = fechaAlta;
    }
}
