package es.ujaen.dae.notificacionincidencias.entidades;

import com.fasterxml.jackson.annotation.JsonCreator;
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

    @JsonCreator
    public CoordenadasGPS(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getlat() {
        return lat;
    }

    public double getlon() {
        return lon;
    }
}
