package com.uasz.gestion_suivi_evaluation_service.config;

import com.uasz.gestion_suivi_evaluation_service.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                /*
                 * =========================================================
                 * CSRF
                 * =========================================================
                 */

                .csrf(AbstractHttpConfigurer::disable)

                /*
                 * =========================================================
                 * CORS
                 * =========================================================
                 */

                .cors(cors -> cors.configurationSource(request -> {

                    CorsConfiguration config =
                            new CorsConfiguration();

                    config.setAllowedOrigins(List.of(

                            "http://localhost:5173",
                            "http://localhost:5174",
                            "http://localhost:5175",

                            "http://127.0.0.1:5173",
                            "http://127.0.0.1:5174",
                            "http://127.0.0.1:5175"
                    ));

                    config.setAllowedMethods(List.of(
                            "GET",
                            "POST",
                            "PUT",
                            "PATCH",
                            "DELETE",
                            "OPTIONS"
                    ));

                    config.setAllowedHeaders(
                            List.of("*")
                    );

                    config.setExposedHeaders(List.of(
                            "Authorization",
                            "Content-Disposition"
                    ));

                    config.setAllowCredentials(true);

                    config.setMaxAge(3600L);

                    return config;
                }))

                /*
                 * =========================================================
                 * SESSION
                 * =========================================================
                 */

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                /*
                 * =========================================================
                 * AUTHORIZATION
                 * =========================================================
                 */

                .authorizeHttpRequests(auth -> auth

                        /*
                         * OPTIONS
                         */

                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()

                        .requestMatchers("/error")
                        .permitAll()

                        /*
                         * RAPPORTS
                         */

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/rapports/**"
                        )
                        .hasAnyRole(
                                "ADMINISTRATEUR",
                                "ENSEIGNANT",
                                "ETUDIANT"
                        )

                        /*
                         * CONSULTATION
                         */

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/suivi/**",
                                "/api/indicateurs/**",
                                "/api/historique/**",
                                "/api/evaluations/**"
                        )
                        .hasAnyRole(
                                "ADMINISTRATEUR",
                                "ENSEIGNANT",
                                "ETUDIANT"
                        )

                        /*
                         * CREATION
                         */

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/suivi/**",
                                "/api/evaluations/**"
                        )
                        .hasRole("ENSEIGNANT")

                        /*
                         * MODIFICATION
                         */

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/suivi/**",
                                "/api/evaluations/**"
                        )
                        .hasRole("ENSEIGNANT")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/suivi/**",
                                "/api/evaluations/**"
                        )
                        .hasRole("ENSEIGNANT")

                        /*
                         * SUPPRESSION
                         */

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/suivi/**",
                                "/api/evaluations/**"
                        )
                        .hasRole("ADMINISTRATEUR")

                        /*
                         * AUTRES
                         */

                        .anyRequest()
                        .authenticated()
                )

                /*
                 * =========================================================
                 * JWT FILTER
                 * =========================================================
                 */

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}