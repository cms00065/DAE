package es.ujaen.dae.notificacionincidencias.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

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
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Permitir cualquier origen
        config.setAllowedOriginPatterns(List.of("*"));

        // Métodos HTTP permitidos
        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        // Cabeceras permitidas (JWT usa Authorization)
        config.setAllowedHeaders(List.of(
                "Authorization", "Content-Type"
        ));

        // Permitir envío de credenciales (JWT en header)
        config.setAllowCredentials(true);

        // Exponer cabeceras si fuese necesario
        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.disable())
                .addFilterAfter(new FiltroAutenticacionJwt(), UsernamePasswordAuthenticationFilter.class)
                //.httpBasic(httpBasic -> httpBasic.realmName("notificacionincidencias"))

                // Solo un usuario ADMIN o el propio usuario puede ver sus datos
                .authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.GET, "/usuarios/{email}")
                        .access(new WebExpressionAuthorizationManager("hasRole('ADMIN') or #email == principal"))

                        // Solo un usuario ADMIN puede actualizar el estado de una incidencia
                        .requestMatchers(HttpMethod.POST, "/incidencias/{id}/actualizarEstado/{nuevoEstado}").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()

                        .requestMatchers(HttpMethod.POST, "/auth/autenticacion").permitAll()

                        // Todos los usuarios pueden crear incidencias
                        .requestMatchers(HttpMethod.POST, "/incidencias/registro").hasAnyRole("ADMIN", "CIUDADANO")

                        .requestMatchers(HttpMethod.GET, "/incidencias/**").authenticated()

                        .requestMatchers(HttpMethod.POST, "/incidencias/{id}/foto").hasAnyRole("ADMIN", "CIUDADANO")

                        //Cualquier usuario autenticado puede consultar el tipo de incidencia
                        .requestMatchers(HttpMethod.GET, "/tiposincidencia/**").authenticated()

                        //Solo un usuario ADMIN puede dar de alta o de baja un tipo de incidencia
                        .requestMatchers(HttpMethod.POST, "/tiposincidencia/alta").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/tiposincidencia/baja/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .build();
    }
}