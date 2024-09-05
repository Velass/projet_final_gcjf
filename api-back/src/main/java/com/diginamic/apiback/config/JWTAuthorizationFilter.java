package com.diginamic.apiback.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

@Configuration
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtConfig jwtConfig;

    /**
     * Filtre qui vérifie la présence d'un jeton JWT dans un cookie et qui
     * authentifie l'utilisateur si le jeton est valide.
     * 
     */
    @Override
    @SuppressWarnings({ "deprecation", "null" })
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        // Recherche du jeton par Cookie
        if (req.getCookies() != null) {
            Stream.of(req.getCookies())
                    .filter(cookie -> cookie.getName().equals(jwtConfig.getCookie()))
                    .map(Cookie::getValue)
                    .forEach(token -> {
                        Claims body = Jwts.parser()
                                .setSigningKey(jwtConfig.getSecretKey())
                                .parseClaimsJws(token)
                                .getBody();
                        String username = body.getSubject();
                        String role = body.get("role", String.class);
                        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

                        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null,
                                authorities);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    });
        }
        chain.doFilter(req, res);
    }
}