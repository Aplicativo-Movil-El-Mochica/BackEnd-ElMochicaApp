package org.mochica.AppDelivery.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // Eliminar allowedOrigins porque estás usando allowedOriginPatterns
        corsConfiguration.setAllowedOriginPatterns(Arrays.asList("*")); // Permitir cualquier origen usando patrones

        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(Arrays.asList("*"));  // Permitir todos los headers
        corsConfiguration.setAllowCredentials(false);  // Activar credenciales, necesario si envías cookies o tokens

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);  // Aplicar las reglas a todas las rutas

        return new CorsFilter(source);
    }
}
