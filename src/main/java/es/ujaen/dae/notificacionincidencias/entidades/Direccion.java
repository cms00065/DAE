package es.ujaen.dae.notificacionincidencias.entidades;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;

/**
 * Representa una dirección postal embebida en Usuario.
 */
@Embeddable
public class Direccion {

    @NotBlank(message = "La vía no puede estar vacía")
    private String via;

    @NotBlank(message = "El número no puede estar vacío")
    private String numero;

    private String pisoPuerta;

    @NotBlank(message = "La ciudad no puede estar vacía")
    private String ciudad;

    @NotBlank(message = "El código postal no puede estar vacío")
    private String cp;

    public Direccion() {
    }

    public Direccion(String via, String numero, String pisoPuerta, String ciudad, String cp) {
        this.via = via;
        this.numero = numero;
        this.pisoPuerta = pisoPuerta;
        this.ciudad = ciudad;
        this.cp = cp;
    }

    // Métodos tipo record, como en tus ejemplos anteriores
    public String via() {
        return via;
    }

    public String numero() {
        return numero;
    }

    public String pisoPuerta() {
        return pisoPuerta;
    }

    public String ciudad() {
        return ciudad;
    }

    public String cp() {
        return cp;
    }
}

