package org.example.lab06_20222297.config;

import org.example.lab06_20222297.entity.Mesa;
import org.example.lab06_20222297.entity.Rol;
import org.example.lab06_20222297.entity.Usuario;
import org.example.lab06_20222297.repository.MesaRepository;
import org.example.lab06_20222297.repository.RolRepository;
import org.example.lab06_20222297.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;
    
    @Autowired
    private MesaRepository mesaRepository;

    @Override
    public void run(String... args) throws Exception {
        // Crear roles si no existen
        if (rolRepository.findByNombre("ADMIN").isEmpty()) {
            Rol adminRole = new Rol();
            adminRole.setNombre("ADMIN");
            rolRepository.save(adminRole);
        }

        if (rolRepository.findByNombre("USUARIO").isEmpty()) {
            Rol userRole = new Rol();
            userRole.setNombre("USUARIO");
            rolRepository.save(userRole);
        }

        // Crear usuario administrador si no existe
        if (usuarioRepository.findByCorreo("admin@admin.com").isEmpty()) {
            Rol rolAdmin = rolRepository.findByNombre("ADMIN").get();

            Usuario admin = new Usuario();
            admin.setNombre("Administrador");
            admin.setCorreo("admin@admin.com");
            admin.setPassword("admin123"); // Contraseña en texto plano
            admin.setRol(rolAdmin);

            usuarioRepository.save(admin);
            
            System.out.println("Usuario administrador creado:");
            System.out.println("Email: admin@admin.com");
            System.out.println("Password: admin");
        }
        
        // Crear mesas de prueba para el Oktoberfest si no existen
        if (mesaRepository.count() == 0) {
            // Crear 8 mesas con diferentes capacidades
            for (int i = 1; i <= 8; i++) {
                Mesa mesa = new Mesa();
                mesa.setNumero(i);
                
                // Variar las capacidades: algunas de 2, algunas de 4 personas
                if (i <= 2) {
                    mesa.setCapacidad(2);
                } else if (i <= 4) {
                    mesa.setCapacidad(3);
                } else {
                    mesa.setCapacidad(4);
                }
                
                mesa.setDisponible(true);
                mesaRepository.save(mesa);
            }
            
            System.out.println("Mesas de prueba para Oktoberfest creadas:");
            System.out.println("- 2 mesas de 2 personas (mesas 1-2)");
            System.out.println("- 2 mesas de 3 personas (mesas 3-4)");
            System.out.println("- 4 mesas de 4 personas (mesas 5-8)");
        }
    }
}