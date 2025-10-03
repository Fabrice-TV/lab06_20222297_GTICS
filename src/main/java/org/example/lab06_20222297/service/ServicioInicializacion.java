package org.example.lab06_20222297.service;

import org.example.lab06_20222297.entity.Rol;
import org.example.lab06_20222297.entity.Usuario;
import org.example.lab06_20222297.repository.RolRepository;
import org.example.lab06_20222297.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Order(2) // Se ejecuta después de ServicioActualizacionPasswords
public class ServicioInicializacion implements CommandLineRunner {
    
    @Autowired
    private RolRepository rolRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("🚀 Iniciando configuración de la base de datos...");
        inicializarRoles();
        System.out.println("✅ Configuración de la base de datos completada!");
    }
    
    private void inicializarRoles() {
        // Crear rol ADMIN si no existe
        if (!rolRepository.existsByNombre("ADMIN")) {
            Rol rolAdmin = new Rol("ADMIN");
            rolRepository.save(rolAdmin);
            System.out.println("Rol ADMIN creado");
        }
        
        // Crear rol USUARIO si no existe
        if (!rolRepository.existsByNombre("USUARIO")) {
            Rol rolUsuario = new Rol("USUARIO");
            rolRepository.save(rolUsuario);
            System.out.println("Rol USUARIO creado");
        }
        
        // Crear rol VISITANTE si no existe
        if (!rolRepository.existsByNombre("VISITANTE")) {
            Rol rolVisitante = new Rol("VISITANTE");
            rolRepository.save(rolVisitante);
            System.out.println("Rol VISITANTE creado");
        }
    }
}