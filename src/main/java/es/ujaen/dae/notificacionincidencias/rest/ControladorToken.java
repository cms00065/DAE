package es.ujaen.dae.notificacionincidencias.rest;

import es.ujaen.dae.notificacionincidencias.rest.dto.dtoAutenticacionUsuario;
import es.ujaen.dae.notificacionincidencias.util.UtilJwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.stream.Collectors;

/**
 * @author cms00065
 */
@RestController
@RequestMapping("/auth")
public class ControladorToken {
    final AuthenticationManager authenticationManager;

    //Tiempo de expiración del token en minutos
    @Value("${tiempoExpiracionTokenJwtMin}")
    int tiempoExpiracionToken;

    public ControladorToken(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * @brief Endpoint de autenticación. Recibe JSON con email y clave, las valida y genera token JWT que incluye los roles del usuario
     * @param emailClaveUsuario DTO con email y clave del usuario
     * @return Código de éxito con token JWT o error si las credenciales son incorrectas
     */
    @PostMapping("/autenticacion")
    public ResponseEntity<String> getToken(@RequestBody dtoAutenticacionUsuario emailClaveUsuario){
        //Autentico para comprobar credenciales y extraer roles
        Authentication authentication;

        try{
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(emailClaveUsuario.email(), emailClaveUsuario.clave()));
        }catch (BadCredentialsException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        //Extraigo roles y convierto a una cadena separada por comas
        String roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

        //Creo y devuelvo token Jwt
        return ResponseEntity.ok(UtilJwt.crearToken(emailClaveUsuario.email(), Collections.singletonMap("roles", roles), tiempoExpiracionToken));
    }
}
