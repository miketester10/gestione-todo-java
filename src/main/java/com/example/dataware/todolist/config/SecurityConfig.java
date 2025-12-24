package com.example.dataware.todolist.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.dataware.todolist.jwt.filter.JwtAccessFilter;
import com.example.dataware.todolist.jwt.filter.JwtRefreshFilter;

@Configuration
@EnableWebSecurity // Abilita sicurezza web
@EnableMethodSecurity // Abilita annotation @PreAuthorize
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(
            HttpSecurity http,
            JwtAccessFilter jwtAccessFilter,
            JwtRefreshFilter jwtRefreshFilter) throws Exception {

        http.csrf((csfr) -> csfr.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAccessFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtRefreshFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}