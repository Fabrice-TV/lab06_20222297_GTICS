package org.example.lab06_20222297.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Para desarrollo - contraseñas en texto plano
        return NoOpPasswordEncoder.getInstance();
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize

                .requestMatchers("/heroes/**").permitAll()
                .requestMatchers("/css/**", "/images/**").permitAll()
                .requestMatchers("/", "/auth/login", "/registro").permitAll()
                .requestMatchers("/festividades", "/combate-angamos", "/senor-milagros", "/cancion-criolla", "/halloween", "/oktoberfest").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/usuario/**", "/juegos/**", "/dashboard").hasAnyRole("USUARIO", "ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/process-login")
                .defaultSuccessUrl("/dashboard", false)
                .failureUrl("/auth/login?error=true")
                .usernameParameter("correo")
                .passwordParameter("password")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            );
        
        return http.build();
    }
}