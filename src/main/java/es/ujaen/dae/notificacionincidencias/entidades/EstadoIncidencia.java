package es.ujaen.dae.notificacionincidencias.entidades;

public enum EstadoIncidencia {
    PENDIENTE,
    EN_EVALUACION,
    RESUELTA;

    public boolean isPendiente(){ return this == PENDIENTE; }
    public boolean isEnEvaluacion(){ return this == EN_EVALUACION; }
    public boolean isResuelta(){ return this == RESUELTA; }
}
