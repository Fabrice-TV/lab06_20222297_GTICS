package org.example.lab06_20222297.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    
    @GetMapping("/auth/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                       @RequestParam(value = "logout", required = false) String logout,
                       Model model) {
        
        model.addAttribute("titulo", "Iniciar Sesión - Festival de Octubre");
        
        if (error != null) {
            model.addAttribute("errorMessage", 
                "Credenciales incorrectas. Verifica tu correo y contraseña.");
        }
        
        if (logout != null) {
            model.addAttribute("successMessage", 
                "Has cerrado sesión correctamente. ¡Esperamos verte pronto!");
        }
        
        return "auth/login";
    }
}