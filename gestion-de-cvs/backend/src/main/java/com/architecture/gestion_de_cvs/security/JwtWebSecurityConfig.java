package com.architecture.gestion_de_cvs.security;

import java.util.Arrays;
import java.util.Set;

import com.architecture.gestion_de_cvs.repository.XUserRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.DispatcherType;

/**
 * Configuration de Spring Security.
 */
@Configuration
@EnableWebSecurity
public class JwtWebSecurityConfig {

    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private XUserRepository userRepo;

    @Autowired
    private JwtProvider jwtTokenProvider;

    @PostConstruct
    public void init() {
        logger.info("+++ SPRING SECURITY JWT");
    }

    /*
     * Configuration globale du mécanisme CORS (pour éviter
     * d'utiliser l'annotation @CrossOrigin).
     */

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /*
     * Définition de la politique de sécurité.
     */

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Pas de vérification CSRF (cross site request forgery)
        http.csrf(config -> {
            config.disable();
        });

        // Spring security ne doit pas gérer les sessions
        http.sessionManagement(config -> {
            config.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        });

        // Les requêtes CORS doivent être acceptées par SpringSecurity
        http.cors(Customizer.withDefaults());

        // Déclaration des end-points
        http.authorizeHttpRequests(config -> {
            config.dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll();
            // Autoriser l'accès aux fichiers statiques du frontend
            config.requestMatchers("/frontend/**").permitAll();
            

            // Pour tous
            config.requestMatchers("/secu-users/login").permitAll();
            config.requestMatchers("/secu-users/signup").permitAll();
            config.requestMatchers("/secu-users/signup-with-invitation").permitAll();
            config.requestMatchers("/secu-users/**").authenticated();

            // Invitations - accès public pour consulter et accepter les invitations
            config.requestMatchers(HttpMethod.GET, "/api/invitations/**").permitAll();
            config.requestMatchers(HttpMethod.POST, "/api/invitations/*/accept").permitAll();

            // Consultation publique (GET) - lecture seule
            config.requestMatchers(HttpMethod.GET, "/api/persons/**").permitAll();
            config.requestMatchers(HttpMethod.GET, "/api/activities/**").permitAll();

            // Création, modification, suppression - nécessite authentification
            config.requestMatchers(HttpMethod.POST, "/api/persons/**").authenticated();
            config.requestMatchers(HttpMethod.PUT, "/api/persons/**").authenticated();
            config.requestMatchers(HttpMethod.DELETE, "/api/persons/**").authenticated();
            config.requestMatchers(HttpMethod.POST, "/api/activities/**").authenticated();
            config.requestMatchers(HttpMethod.PUT, "/api/activities/**").authenticated();
            config.requestMatchers(HttpMethod.DELETE, "/api/activities/**").authenticated();

            // Tout le reste nécessite une authentification
            config.anyRequest().authenticated();
        });

        // Pas vraiment nécessaire
        http.exceptionHandling(config -> {
            config.accessDeniedPage("/secu-users/login");
        });

        // Mise en place du filtre JWT
        JwtFilter customFilter = new JwtFilter(jwtTokenProvider);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /*
     * Définir le gestionnaire d'authentification. Nous utilisons la version
     * DaoAuthenticationProvider qui récupère les informations à partir du
     * UserDetailsService que nous avons défini avant.
     */

    @Bean
    public AuthenticationManager myAuthenticationManager(//
            @Autowired PasswordEncoder encoder, //
            @Autowired UserDetailsService userDetailsService) {
        var authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(encoder);
        return new ProviderManager(authProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

}