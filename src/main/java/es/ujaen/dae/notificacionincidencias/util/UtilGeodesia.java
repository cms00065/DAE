package es.ujaen.dae.notificacionincidencias.util;

import es.ujaen.dae.notificacionincidencias.entidades.CoordenadasGPS;

public class UtilGeodesia {
    private static final double radioTierraMetros = 6_371_000.0;

    /**
     * @brief Calcula la distancia en metros entre dos puntos GPS usando la fórmula de Haversine
     * @param lat1 Latitud del primer punto en grados
     * @param lon1 Longitud del primer punto en grados
     * @param lat2 Latitud del segundo punto en grados
     * @param lon2 Longitud del segundo punto en grados
     * @return Distancia aproximada en metros entre ambos puntos
     */
    public static double distanciaMetros(double lat1, double lon1, double lat2, double lon2) {
        //Convierto grados a radianes
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        double difLat = lat2Rad - lat1Rad;
        double difLon = lon2Rad - lon1Rad;

        //Aplico fórmula de Haversine
        double a = Math.sin(difLat / 2) * Math.sin(difLat / 2) + Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.sin(difLon / 2) * Math.sin(difLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return radioTierraMetros * c;
    }

    /**
     * @brief Sobrecarga del método anterior recibiendo directamente dos objetos CoordenadasGPS
     * @param origen Coordenadas del primer punto
     * @param destino Coordenadas del segundo punto
     * @return Distancia aproximada en metros entre ambos puntos
     */
    public static double distanciaMetros(CoordenadasGPS origen, CoordenadasGPS destino){
        return distanciaMetros(origen.getlat(), origen.getlon(), destino.getlat(), destino.getlon());
    }
}
