package es.ujaen.dae.notificacionincidencias.entidades;

public enum Rol {
    ADMIN,
    CIUDADANO;

    public boolean isAdmin() {
        return this == ADMIN;
    }
}
