package com.diginamic.apiback.config;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseCookie;

import com.diginamic.apiback.models.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Configuration
public class JWTCookieConfig {
        @Autowired
        private JwtConfig jwtConfig;

        /**
         * Crée un cookie JWT à partir d'un objet User
         * 
         * @param user l'utilisateur
         * @return un cookie JWT
         */
        @SuppressWarnings({ "deprecation" })
        public String buildJWTCookie(User user) {
                byte[] secretKeyBytes = jwtConfig.getSecretKey().getEncoded();
                String jetonJWT = Jwts.builder()
                                .setSubject(user.getEmail())
                                .addClaims(Map.of("role", user.getRole(), "user_id", user.getId(), "firstname",
                                                user.getFirstName(),
                                                "lastname", user.getLastName()))
                                .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getExpireIn() * 1000))
                                .signWith(SignatureAlgorithm.HS512, secretKeyBytes)
                                .compact();
                ResponseCookie tokenCookie = ResponseCookie.from(jwtConfig.getCookie(),
                                jetonJWT)
                                .httpOnly(true)
                                .maxAge(jwtConfig.getExpireIn()) // La durée de validité est en secondes
                                .path("/")
                                .build();
                return tokenCookie.toString();
        }
}
