package org.example.lab06_20222297.controller;

import org.example.lab06_20222297.entity.Rol;
import org.example.lab06_20222297.entity.Usuario;
import org.example.lab06_20222297.repository.RolRepository;
import org.example.lab06_20222297.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
public class RegistroController {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private RolRepository rolRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("titulo", "Registrarse - Festival de Octubre");
        model.addAttribute("usuario", new Usuario());
        return "auth/registro";
    }
    
    @PostMapping("/registro")
    public String procesarRegistro(@Valid Usuario usuario, 
                                 BindingResult result, 
                                 Model model, 
                                 RedirectAttributes redirectAttributes) {
        
        model.addAttribute("titulo", "Registrarse - Festival de Octubre");
        
        // Validar errores de formulario
        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "Por favor corrige los errores en el formulario.");
            return "auth/registro";
        }
        
        // Verificar si el correo ya existe
        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            model.addAttribute("errorMessage", "Ya existe una cuenta con este correo electrónico.");
            return "auth/registro";
        }
        
        try {
            // Obtener o crear el rol USUARIO
            Rol rolUsuario = rolRepository.findByNombre("USUARIO")
                    .orElseGet(() -> {
                        Rol nuevoRol = new Rol("USUARIO");
                        return rolRepository.save(nuevoRol);
                    });
            
            // No encriptar la contraseña (texto plano)
            // usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            // La contraseña ya viene en texto plano desde el formulario
            
            // Asignar el rol
            usuario.setRol(rolUsuario);
            
            // Guardar el usuario
            usuarioRepository.save(usuario);
            
            // Mensaje de éxito y redirección
            redirectAttributes.addFlashAttribute("successMessage", 
                "¡Cuenta creada exitosamente! Ya puedes iniciar sesión.");
            
            return "redirect:/login";
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", 
                "Error al crear la cuenta. Por favor intenta nuevamente.");
            return "auth/registro";
        }
    }
}