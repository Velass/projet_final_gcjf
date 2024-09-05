package com.diginamic.apiback.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {

    @Autowired
    private JWTAuthorizationFilter jwtAuthorizationFilter;
    @Autowired
    private JwtConfig jwtConfig;

    /**
     * Crée un objet PasswordEncoder
     * 
     * @return un objet PasswordEncoder
     */
    @Bean
    PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Crée un objet MvcRequestMatcher.Builder qui permet de définir des règles de
     * sécurité basées sur des requêtes HTTP.
     * 
     * @param introspector un objet HandlerMappingIntrospector
     * @return un objet MvcRequestMatcher.Builder
     */
    @Scope("prototype")
    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    /**
     * Configuration de CORS
     * 
     * @return un objet CorsConfigurationSource
     */
    @Bean
    CorsConfigurationSource corsConfiguration() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Remplacez "*" par l'origine exacte (par exemple, votre frontend)
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); 
        
        // Accepter toutes les méthodes HTTP
        configuration.setAllowedMethods(List.of("*"));
        
        // Accepter tous les en-têtes
        configuration.setAllowedHeaders(List.of("*"));
        
        // Autoriser les informations d'identification (cookies, tokens)
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Configuration de la sécurité
     * 
     * @param http   un objet HttpSecurity
     * @param mvc    un objet MvcRequestMatcher.Builder
     * @param source la configuration CORS
     * @return un objet SecurityFilterChain
     * @throws Exception si une erreur survient
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc,
            @Qualifier("corsConfiguration") CorsConfigurationSource source) throws Exception {

        http.authorizeHttpRequests(
                auth -> auth
                        .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/login/**")).permitAll()
                        .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/**")).authenticated()
                        .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/**")).authenticated()
                        .requestMatchers(mvc.pattern(HttpMethod.PUT, "/api/**")).authenticated()
                        .requestMatchers(mvc.pattern(HttpMethod.DELETE, "/api/**")).authenticated()
                        .anyRequest().authenticated())

                // Spring Security va valoriser un jeton stocké dans un cookie XSRF-TOKEN
                // Le client souhaitant effectuer une requête de modification (POST par exemple)
                // devra valoriser une entête HTTP "X-XSRF-TOKEN" avec le jeton.

                .csrf(csrf -> csrf.disable())
                // .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                // .csrfTokenRequestHandler(new XorCsrfTokenRequestAttributeHandler()::handle))

                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .cors(cors -> cors.configurationSource(source))
                .logout(logout -> logout
                        // // en cas de succès un OK est envoyé
                        .logoutSuccessHandler((req, resp, auth) -> resp.setStatus(HttpStatus.OK.value()))
                        // // suppression du cookie d'authentification
                        .deleteCookies(jwtConfig.getCookie()));
        return http.build();
    }

}