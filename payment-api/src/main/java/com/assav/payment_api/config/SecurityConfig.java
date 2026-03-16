package com.assav.payment_api.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Desativamos CSRF pois usamos Tokens (APIs REST)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated() // Exige que TODAS as rotas tenham um Token válido
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {})); // Ativa a verificação JWT
        return http.build();
    }
}