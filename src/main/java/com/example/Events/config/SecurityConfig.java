package com.example.Events.config;

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
        http.authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/test").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(customizer -> customizer.disable())
                .formLogin(customizer -> customizer.disable())
                .httpBasic(customizer -> customizer.disable());

        return http.build();
    }

}
