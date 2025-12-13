package es.ujaen.dae.notificacionincidencias.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

@Configuration
public class ServicioSeguridad {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration authConf) throws Exception {
        return authConf.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.disable())
                .httpBasic(httpBasic -> httpBasic.realmName("notificacionincidencias"))
                .authorizeHttpRequests(request -> request
                        // Solo un usuario ADMIN puede actualizar el estado de una incidencia
                        .requestMatchers(HttpMethod.POST, "/incidencias/{id}/actualizarEstado").hasRole("ADMIN")

                        // Todos los usuarios pueden crear incidencias
                        .requestMatchers(HttpMethod.POST, "/incidencias").hasAnyRole("ADMIN", "CIUDADANO")

                        .requestMatchers(HttpMethod.GET, "/incidencias/**").authenticated()

                        // Solo un usuario ADMIN o el propio usuario puede ver sus datos
                        .requestMatchers(HttpMethod.GET, "/usuarios/{id}").access(new WebExpressionAuthorizationManager("hasRole('ADMIN') or #id == principal.username"))

                        .anyRequest().authenticated()
                )
                .build();
    }
}