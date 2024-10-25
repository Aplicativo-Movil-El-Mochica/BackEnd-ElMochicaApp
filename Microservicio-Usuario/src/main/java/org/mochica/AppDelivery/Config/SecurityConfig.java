package org.mochica.AppDelivery.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig  {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private LoggingFilter loggingFilter;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
        logger.debug("SecurityConfig initialized.");
        // We don't need CSRF for this example

        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)  // Desactivar CSRF, ya que usamos JWT

                .authorizeHttpRequests((requests) ->
                        requests
                                .requestMatchers("/user/register", "/user/login").permitAll()  // Permitir el registro y login sin autenticación
                                .anyRequest().authenticated()  // Todas las demás solicitudes deben estar autenticadas
                )
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));  // No necesitamos sesiones, usaremos JWT

        // Añadir filtro para loggear las solicitudes y respuestas
        httpSecurity.addFilterBefore(loggingFilter, UsernamePasswordAuthenticationFilter.class);

        // Añadir filtro para validar el JWT en cada solicitud
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        logger.debug("AuthenticationManager invoked.");
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.debug("PasswordEncoder invoked.");
        return new BCryptPasswordEncoder();  // Cambiado a BCrypt para mayor seguridad
    }
}
