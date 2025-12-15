package es.ujaen.dae.notificacionincidencias.entidades;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonCreator
    public Direccion(String via, String numero, String pisoPuerta, String ciudad, String cp) {
        this.via = via;
        this.numero = numero;
        this.pisoPuerta = pisoPuerta;
        this.ciudad = ciudad;
        this.cp = cp;
    }

    public String getVia() {
        return via;
    }

    public String getNumero() {
        return numero;
    }

    public String getPisoPuerta() {
        return pisoPuerta;
    }

    public String getCiudad() {
        return ciudad;
    }

    public String getCcp() {
        return cp;
    }
}

