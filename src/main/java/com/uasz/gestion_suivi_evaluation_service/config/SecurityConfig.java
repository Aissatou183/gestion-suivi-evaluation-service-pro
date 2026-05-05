package com.uasz.gestion_suivi_evaluation_service.config;

import com.uasz.gestion_suivi_evaluation_service.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of(
                            "http://localhost:5173", "http://localhost:5174", "http://localhost:5175",
                            "http://127.0.0.1:5173", "http://127.0.0.1:5174", "http://127.0.0.1:5175"
                    ));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setExposedHeaders(List.of("Authorization"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/suivi").hasRole("ENSEIGNANT")
                        .requestMatchers(HttpMethod.POST, "/api/evaluations").hasRole("ENSEIGNANT")
                        .requestMatchers(HttpMethod.POST, "/api/historique").hasAnyRole("ADMINISTRATEUR", "ENSEIGNANT", "ETUDIANT")

                        .requestMatchers(HttpMethod.GET, "/api/rapports/**").hasAnyRole("ADMINISTRATEUR", "ENSEIGNANT")
                        .requestMatchers(HttpMethod.GET, "/api/**").hasAnyRole("ADMINISTRATEUR", "ENSEIGNANT", "ETUDIANT")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
