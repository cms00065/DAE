package es.ujaen.dae.notificacionincidencias.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "es.ujaen.dae.notificacionincidencias.servicios")
public class NotificacionIncidencias {

    public static void main(String[] args) {
        SpringApplication.run(NotificacionIncidencias.class, args);
    }

}
