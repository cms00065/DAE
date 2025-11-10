package es.ujaen.dae.notificacionincidencias.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * @author gcg00035
 */
@Entity
@Table(name = "Usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank
    private String nombre;

    private String apellidos;

    private LocalDate fechaNacimiento;

    @NotNull
    @Embedded
    private Direccion direccion;

    @Pattern(
            regexp = "^(\\+34|0034|34)?[6789]\\d{8}$",
            message = "No es un número de teléfono válido"
    )
    private String telefono;

    @Email(message = "Debe ser un correo electrónico válido")
    private String email;

    @NotBlank(message = "El login no puede estar vacío")
    private String login;

    @NotBlank(message = "La clave no puede estar vacía")
    private String hashClave;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Rol rol;

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

    public boolean verificarClave(String clave) {
        return clave.equals(hashClave);
    }

    public void cambiarClave(String clave) {
        hashClave = clave;
    }

    // -- SETTERS --

    public void nombre(String nombre) {
        this.nombre = nombre;
    }

    public void apellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public void fechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public void direccion(Direccion direccion) {
        this.direccion = direccion;
    }

    public void telefono(String telefono) {
        this.telefono = telefono;
    }

    public void email(String email) {
        this.email = email;
    }

    public void login(String login) {
        this.login = login;
    }

    public void rol(Rol rol) {
        this.rol = rol;
    }

}
