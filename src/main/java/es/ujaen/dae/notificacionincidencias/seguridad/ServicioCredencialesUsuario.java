package es.ujaen.dae.notificacionincidencias.seguridad;


import es.ujaen.dae.notificacionincidencias.entidades.Rol;
import es.ujaen.dae.notificacionincidencias.entidades.Usuario;
import es.ujaen.dae.notificacionincidencias.servicios.ServicioUsuarios;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;




/**
 * @author gcg00035
 */
@Service
public class ServicioCredencialesUsuario implements UserDetailsService {

    @Autowired
    ServicioUsuarios servicioUsuarios;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = servicioUsuarios.buscarUsuario(email).orElseThrow(() -> new UsernameNotFoundException(""));

        return User.withUsername(usuario.email())
                .password(usuario.hashClave())
                .roles(usuario.rol().equals(Rol.ADMIN) ? "ADMIN": "CIUDADANO")
                .build();
    }
}

