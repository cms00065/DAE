package es.ujaen.dae.notificacionincidencias.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * @author jma00081
 */
@Entity
public class Incidencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private LocalDate fecha;
    private LocalDate fechaUltimaActualizacion;

    @NotBlank (message = "La descripción no puede estar vacía")
    private String descripcion;

    @Pattern(regexp = "^C/\\s.+$", message = "La localización debe empezar por 'C/ ' seguido del nombre de la calle")
    private String localizacion;

    @Enumerated
    private EstadoIncidencia estado;

    @NotNull
    @Embedded
    private CoordenadasGPS ubicacionGPS;

    @NotNull
    @ManyToOne
    private TipoIncidencia tipo;

    @NotNull
    @ManyToOne
    private Usuario creador;

    public Incidencia() {}

    public Incidencia(LocalDate fecha, String descripcion, String localizacion, EstadoIncidencia estado, CoordenadasGPS ubicacionGPS, TipoIncidencia tipo, Usuario creador) {
        this.fecha = fecha;
        this.fechaUltimaActualizacion = fecha;
        this.descripcion = descripcion;
        this.localizacion = localizacion;
        this.estado = estado;
        this.ubicacionGPS = ubicacionGPS;
        this.tipo = tipo;
        this.creador = creador;
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

    public CoordenadasGPS ubicacionGPS() {return ubicacionGPS;}

    public TipoIncidencia tipo() {
        return tipo;
    }

    public Usuario creador() {
        return creador;
    }

    public void id(int id) {
        this.id = id;
    }

    public void fecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void fechaUltimaActualizacion(LocalDate fechaUltimaActualizacion) {
        this.fechaUltimaActualizacion = fechaUltimaActualizacion;
    }

    public void descripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void localizacion(String localizacion) {
        this.localizacion = localizacion;
    }

    public void estado(EstadoIncidencia estado) {
        this.estado = estado;
    }

    public void ubicacionGPS(CoordenadasGPS ubicacionGPS) {
        this.ubicacionGPS = ubicacionGPS;
    }

    public void tipo(TipoIncidencia tipo) {
        this.tipo = tipo;
    }

    /**
     * Indica si el usuario tiene permiso para borrar la incidencia
     * Solo los usuarios con rol ADMIN pueden borrar la incidencia
     * Si un usuario no es ADMIN, solo puede borrar la incidencia si es el creador y la incidencia está en estado PENDIENTE
     * @param usuario Usuario que solicita el borrado
     * @return true si el usuario cumple las condiciones, false en caso contrario
     */
    public boolean puedeBorrar(Usuario usuario) {
        return (usuario.rol() == Rol.ADMIN) || (usuario.id() == this.creador.id() && estado == EstadoIncidencia.PENDIENTE);
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

