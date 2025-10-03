package org.example.lab06_20222297.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
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
                // Recursos estáticos
                .requestMatchers("/css/**", "/images/**").permitAll()
                // Páginas públicas
                .requestMatchers("/", "/auth/login", "/registro").permitAll()
                // Festividades: solo índice y combate-angamos son públicos
                .requestMatchers("/festividades", "/combate-angamos").permitAll()
                // Festividades que requieren autenticación
                .requestMatchers("/senor-milagros", "/cancion-criolla", "/halloween", "/oktoberfest").authenticated()
                // Héroes: TODA la gestión de héroes es SOLO para ADMIN
                .requestMatchers("/heroes/**").hasRole("ADMIN")  // Solo ADMIN puede ver y gestionar héroes
                // Intenciones: usuarios autenticados pueden crear, ADMIN puede administrar
                .requestMatchers("/intenciones/admin/**").hasRole("ADMIN")  // Solo ADMIN puede administrar intenciones
                .requestMatchers("/intenciones/**").hasAnyRole("USUARIO", "ADMIN")  // Usuarios autenticados pueden crear intenciones
                // Administración requiere ADMIN
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // Usuario y juegos requieren autenticación
                .requestMatchers("/usuario/**", "/juegos/**", "/dashboard").hasAnyRole("USUARIO", "ADMIN")
                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/process-login")
                .defaultSuccessUrl("/dashboard", true)
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