package org.universaldoctor.msorchestrator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.universaldoctor.msorchestrator.converter.KeycloakRealmRoleConverter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());

        http
                .csrf().disable() // <--- AGGIUNGI QUESTO
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/webjars/**").permitAll()
                        .requestMatchers("/favicon.ico").permitAll()
                        .requestMatchers("/login-vero").permitAll()
                        .requestMatchers(
                                "/swagger-ui.html",          // questa è la pagina iniziale
                                "/swagger-ui/**",            // risorse JS, CSS, ecc.
                                "/v3/api-docs/**"            // OpenAPI JSON
                        ).permitAll()
                        .requestMatchers("/testTwo/auth/**").permitAll()
                        .requestMatchers("/ping").permitAll()
                        .requestMatchers("/profession/**").permitAll()
                        .requestMatchers("/testTwo/test-body").hasRole("employee")
                        .requestMatchers("/testOne/**").hasRole("boss")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter))
                );

        return http.build();
    }

}
