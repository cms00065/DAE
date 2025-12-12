package es.ujaen.dae.notificacionincidencias.seguridad;

import es.ujaen.dae.notificacionincidencias.util.UtilJwt;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;


public class FiltroAutenticacionJwt  extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Si hay cabecera, extraer la información de autenticación/autorización y configurar en Spring Security
        String authorizationHeader = request.getHeader(AUTHORIZATION);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            Claims claims = null;
            try {
                // Extraer los claims del token a continuación de "Bearer "
                claims = UtilJwt.extraerContenido(authorizationHeader.substring(7));
            }
            catch (JwtException e) {
                // Token incorrecto o expirado, devolver código de respuesta UNAUTHORIZED y
                // y cortar la cadena de filtrado de Spring Security
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // Introducir la autenticación en Spring Security
            var authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(claims.get("roles", String.class));

            var authenticationToken = new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request, response);
    }

}
