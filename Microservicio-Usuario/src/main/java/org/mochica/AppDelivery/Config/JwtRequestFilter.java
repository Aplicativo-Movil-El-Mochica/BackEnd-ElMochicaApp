package org.mochica.AppDelivery.Config;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;  // Servicio para cargar detalles del usuario (en tu caso, desde Firestore)

    @Autowired
    private JwtTokenUtil jwtTokenUtil;  // Utilidad para manejar JWT

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");

        String email = null;
        String jwtToken = null;

        // El token JWT está en la forma "Bearer token". Quitar "Bearer" y obtener el token
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);

            try {
                // Decodificar el JWT token en Base64
                String decodedJwtToken = new String(Base64.getDecoder().decode(jwtToken));

                // Obtener el email (subject) del token
                email = jwtTokenUtil.getEmailFromToken(decodedJwtToken);
            } catch (IllegalArgumentException e) {
                System.out.println("No se pudo obtener el JWT Token");
            } catch (ExpiredJwtException e) {
                System.out.println("El JWT Token ha expirado");
            }
        } else {
            logger.warn("El JWT Token no comienza con Bearer String");
        }

        // Validar el token
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Cargar detalles del usuario desde tu servicio
            UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(email);

            // Validar el token decodificado
            String decodedJwtToken = new String(Base64.getDecoder().decode(jwtToken)); // Decodificar antes de validar
            if (jwtTokenUtil.validateToken(decodedJwtToken, email)) {

                // Crear la autenticación y agregarla al contexto de seguridad
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        chain.doFilter(request, response);
    }
}
