package es.ujaen.dae.notificacionincidencias.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

@Entity
public class Usuario {
    @Id
    @Positive
    int id;

    @NotBlank
    String nombre;

    String apellidos;

    LocalDate fechaNacimiento;

    @Embedded
    Direccion direccion;

    @Pattern(
            regexp = "^(\\+34|0034|34)?[6789]\\d{8}$",
            message = "No es un número de teléfono válido"
    )
    String telefono;

    @Email(message = "Debe ser un correo electrónico válido")
    String email;

    @NotBlank(message = "El login no puede estar vacío")
    String login;

    @NotBlank(message = "La clave no puede estar vacía")
    String hashClave;

    @Enumerated(EnumType.STRING)
    Rol rol;

    public Usuario() {
    }

    public Usuario(String nombre, String apellidos, LocalDate fechaNacimiento, Direccion direccion,
                   String telefono, String email, String login, String hashClave, Rol rol) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.login = login;
        this.hashClave = hashClave;
        this.rol = rol;
    }
    public int id() {
        return id;
    }

    public String nombre() {
        return nombre;
    }

    public String apellidos() {
        return apellidos;
    }

    public LocalDate fechaNacimiento() {
        return fechaNacimiento;
    }

    public Direccion direccion() {
        return direccion;
    }

    public String telefono() {
        return telefono;
    }

    public String email() {
        return email;
    }

    public String login() {
        return login;
    }

    public String hashClave() {
        return hashClave;
    }

    public Rol rol() {
        return rol;
    }
}
