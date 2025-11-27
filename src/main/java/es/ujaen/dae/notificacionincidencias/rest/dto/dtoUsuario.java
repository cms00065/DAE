package es.ujaen.dae.notificacionincidencias.rest.dto;


import es.ujaen.dae.notificacionincidencias.entidades.Direccion;
import es.ujaen.dae.notificacionincidencias.entidades.Rol;

import java.time.LocalDate;

/**
 * @author gcg0035
 */
public record dtoUsuario(int id,
                         String nombre,
                         String apellidos,
                         LocalDate fechaNacimiento,
                         Direccion direccion,
                         String telefono,
                         String email,
                         String login,
                         String clave,
                         Rol rol) {


}
