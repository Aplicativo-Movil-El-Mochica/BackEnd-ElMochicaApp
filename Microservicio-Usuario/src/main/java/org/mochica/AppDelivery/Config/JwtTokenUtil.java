package org.mochica.AppDelivery.Config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.Serializable;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);
    private static final long serialVersionUID = 7383112237L;
    public static final int JWT_TOKEN_VALIDITY = 10*60;         // 10 minutes

    @Value("${jwt.secret}")
    public String secret;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(); // Asegúrate de que `secret` esté en formato adecuado
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //generate token for required data i.e. user details
    public String generateToken(UserDetails userDetails){
        // we can set extra info this claims hashmap and below defined getCustomParamFromToken to get it by passing Map key.
        Map<String, Object> claims = new HashMap<>();
//        claims.put("sub-application", "inventory");
        return doGenerateToken(claims, userDetails.getUsername());
    }

    public String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails){
        final String userName = getUserNameFromToken(token);
        return (!isTokenExpired(token) && userName.equals(userDetails.getUsername()));
    }

    public String getUserNameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public String getAudienceFromToken(String token) {
        return getClaimFromToken(token, Claims::getAudience);
    }

    //retrieve expiration date from jwt token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    //check if the token has expired
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Object getCustomParamFromToken(String token, String param){
        final Claims claims = getAllClaimsFromToken(token);
        logger.debug("Requested param from token: {}", param);
        return claims.get(param);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    //for retrieving any information from token we will need the secret key
    private Claims getAllClaimsFromToken(String token){
        return Jwts.parser().setSigningKey(getSigningKey()).parseClaimsJws(token).getBody();
    }
}
