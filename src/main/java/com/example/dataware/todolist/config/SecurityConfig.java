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
import org.springframework.security.web.context.SecurityContextHolderFilter;

import com.example.dataware.todolist.filter.jwt.JwtAccessFilter;
import com.example.dataware.todolist.filter.jwt.JwtRefreshFilter;
import com.example.dataware.todolist.filter.rateLimiter.RateLimitFilter;

@Configuration
@EnableWebSecurity // Abilita sicurezza web
@EnableMethodSecurity // Abilita annotation @PreAuthorize
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(
            HttpSecurity http,
            RateLimitFilter rateLimitFilter,
            JwtAccessFilter jwtAccessFilter,
            JwtRefreshFilter jwtRefreshFilter) throws Exception {

        http.csrf((csfr) -> csfr.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(rateLimitFilter, SecurityContextHolderFilter.class)
                .addFilterAfter(jwtRefreshFilter, RateLimitFilter.class)
                .addFilterAfter(jwtAccessFilter, JwtRefreshFilter.class);

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}