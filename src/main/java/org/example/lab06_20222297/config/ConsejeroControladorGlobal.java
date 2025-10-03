package org.example.lab06_20222297.config;

import org.example.lab06_20222297.entity.Usuario;
import org.example.lab06_20222297.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Optional;

/**
 * Controlador global que añade automáticamente el usuario autenticado 
 * a todos los modelos de vista en la aplicación
 */
@ControllerAdvice
public class ConsejeroControladorGlobal {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Método que se ejecuta automáticamente en cada petición HTTP
     * para añadir el objeto usuario al modelo si está autenticado
     */
    @ModelAttribute
    public void agregarUsuarioAlModelo(Model modelo, Authentication autenticacion) {
        // Verificar si el usuario está autenticado y no es anónimo
        if (autenticacion != null && autenticacion.isAuthenticated() && !autenticacion.getName().equals("anonymousUser")) {
            String correoElectronico = autenticacion.getName();
            
            // Buscar el usuario en la base de datos por su correo
            Optional<Usuario> usuarioOpcional = usuarioRepository.findByCorreo(correoElectronico);
            
            // Si se encuentra el usuario, añadirlo al modelo para que esté disponible en todos los templates
            if (usuarioOpcional.isPresent()) {
                modelo.addAttribute("usuario", usuarioOpcional.get());
            }
        }
    }
}