package es.ujaen.dae.notificacionincidencias.entidades;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

/**
 * @author jma00081
 */
@Embeddable
public class CoordenadasGPS {
    @NotNull
    double lat;

    @NotNull
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
