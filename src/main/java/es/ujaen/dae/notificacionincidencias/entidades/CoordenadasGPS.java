package es.ujaen.dae.notificacionincidencias.entidades;

import jakarta.validation.constraints.NotBlank;

public class CoordenadasGPS {
    @NotBlank
    double lat;

    @NotBlank
    double lon;

    public CoordenadasGPS() {}

    public CoordenadasGPS(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double lat() {
        return lat;
    }

    public double lon() {
        return lon;
    }
}
