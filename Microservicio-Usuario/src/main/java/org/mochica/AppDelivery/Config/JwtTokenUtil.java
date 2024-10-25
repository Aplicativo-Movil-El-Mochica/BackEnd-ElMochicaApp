package org.mochica.AppDelivery.Config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

    private final SecretKey secret = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    // Validez del token en milisegundos (10 minutos)
    public static final int JWT_TOKEN_VALIDITY = 10 * 60 * 1000;

    // Obtener el email (subject) del JWT token
    public String getEmailFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // Obtener la fecha de expiración del token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // Para obtener cualquier información del token necesitamos la clave secreta
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    // Verificar si el token ha expirado
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    // Generar el JWT token con Base64
    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        String jwt = doGenerateToken(claims, email);
        return Base64.getEncoder().encodeToString(jwt.getBytes());
    }

    // Crear el JWT token
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    // Validar el JWT token
    public Boolean validateToken(String token, String email) {
        String decodedToken = new String(Base64.getDecoder().decode(token)); // Decodificar el token de Base64

        final String username = getEmailFromToken(decodedToken);  // Obtener el email del token
        return (username.equals(email) && !isTokenExpired(decodedToken));
    }
}
