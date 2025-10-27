package es.ujaen.dae.notificacionincidencias.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = "es.ujaen.dae.notificacionincidencias.servicios")
@EntityScan(basePackages = "es.ujaen.dae.notificacionincidencias.entidades")
public class NotificacionIncidencias {

    public static void main(String[] args) {
        SpringApplication.run(NotificacionIncidencias.class, args);
    }

}
