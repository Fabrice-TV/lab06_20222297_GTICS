package org.example.lab06_20222297.controller;

import org.example.lab06_20222297.entity.Intencion;
import org.example.lab06_20222297.entity.Usuario;
import org.example.lab06_20222297.repository.UsuarioRepository;
import org.example.lab06_20222297.service.IntencionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/intenciones")
public class IntencionController {
    
    @Autowired
    private IntencionService intencionService;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    /**
     * Muestra el formulario para registrar una nueva intención
     * Solo usuarios autenticados pueden acceder
     */
    @GetMapping("/nueva")
    @PreAuthorize("isAuthenticated()")
    public String mostrarFormularioNuevaIntencion(Model model, 
                                                 Authentication authentication,
                                                 HttpSession session) {
        
        // Obtener usuario autenticado
        String correoUsuario = authentication.getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correoUsuario);
        
        if (usuarioOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Usuario no encontrado");
            return "redirect:/auth/login";
        }
        
        Usuario usuario = usuarioOpt.get();
        String sessionId = session.getId();
        
        // Verificar si ya existe una intención para esta sesión
        if (intencionService.yaExisteIntencionEnSesion(sessionId)) {
            Optional<Intencion> intencionExistente = intencionService.obtenerIntencionPorUsuarioYSesion(usuario, sessionId);
            if (intencionExistente.isPresent()) {
                model.addAttribute("intencionExistente", intencionExistente.get());
                model.addAttribute("mensajeInfo", "Ya has registrado una intención en esta sesión.");
            }
        }
        
        model.addAttribute("titulo", "Registrar Nueva Intención");
        model.addAttribute("intencion", new Intencion());
        model.addAttribute("usuario", usuario);
        
        return "intenciones/formulario";
    }
    
    /**
     * Procesa el registro de una nueva intención
     */
    @PostMapping("/nueva")
    @PreAuthorize("isAuthenticated()")
    public String procesarNuevaIntencion(@Valid @ModelAttribute Intencion intencion,
                                       BindingResult result,
                                       Authentication authentication,
                                       HttpSession session,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        
        // Obtener usuario autenticado
        String correoUsuario = authentication.getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correoUsuario);
        
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Usuario no encontrado");
            return "redirect:/auth/login";
        }
        
        Usuario usuario = usuarioOpt.get();
        String sessionId = session.getId();
        
        // Validar errores de formulario
        if (result.hasErrors()) {
            model.addAttribute("titulo", "Registrar Nueva Intención");
            model.addAttribute("usuario", usuario);
            model.addAttribute("errorMessage", "Por favor corrige los errores en el formulario.");
            return "intenciones/formulario";
        }
        
        try {
            // Guardar la intención con validaciones
            Intencion intencionGuardada = intencionService.guardarIntencion(
                usuario, 
                intencion.getDescripcion(), 
                sessionId
            );
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "¡Tu intención ha sido registrada exitosamente!");
            
            return "redirect:/intenciones/confirmacion";
            
        } catch (IllegalArgumentException e) {
            model.addAttribute("titulo", "Registrar Nueva Intención");
            model.addAttribute("usuario", usuario);
            model.addAttribute("errorMessage", e.getMessage());
            return "intenciones/formulario";
        } catch (Exception e) {
            model.addAttribute("titulo", "Registrar Nueva Intención");
            model.addAttribute("usuario", usuario);
            model.addAttribute("errorMessage", "Error al guardar la intención. Intenta nuevamente.");
            return "intenciones/formulario";
        }
    }
    
    /**
     * Página de confirmación después de registrar una intención
     */
    @GetMapping("/confirmacion")
    @PreAuthorize("isAuthenticated()")
    public String mostrarConfirmacion(Model model, 
                                    Authentication authentication,
                                    HttpSession session) {
        
        // Obtener usuario autenticado
        String correoUsuario = authentication.getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correoUsuario);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            String sessionId = session.getId();
            
            Optional<Intencion> intencionOpt = intencionService.obtenerIntencionPorUsuarioYSesion(usuario, sessionId);
            if (intencionOpt.isPresent()) {
                model.addAttribute("intencion", intencionOpt.get());
            }
        }
        
        model.addAttribute("titulo", "Intención Registrada");
        return "intenciones/confirmacion";
    }
    
    /**
     * Vista de administración - Solo para ADMIN
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String mostrarVistaAdmin(Model model) {
        
        List<Intencion> todasLasIntenciones = intencionService.obtenerTodasLasIntenciones();
        long totalIntenciones = intencionService.contarTotal();
        
        model.addAttribute("titulo", "Administración de Intenciones");
        model.addAttribute("intenciones", todasLasIntenciones);
        model.addAttribute("totalIntenciones", totalIntenciones);
        
        return "intenciones/admin";
    }
    
    /**
     * Eliminar una intención - Solo para ADMIN
     */
    @PostMapping("/admin/{id}/eliminar")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminarIntencion(@PathVariable Long id, 
                                  RedirectAttributes redirectAttributes) {
        
        boolean eliminado = intencionService.eliminarIntencion(id);
        
        if (eliminado) {
            redirectAttributes.addFlashAttribute("successMessage", 
                "Intención eliminada exitosamente.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error al eliminar la intención.");
        }
        
        return "redirect:/intenciones/admin";
    }
    
    /**
     * Mis intenciones - Para usuarios autenticados
     */
    @GetMapping("/mis-intenciones")
    @PreAuthorize("isAuthenticated()")
    public String mostrarMisIntenciones(Model model, Authentication authentication) {
        
        String correoUsuario = authentication.getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correoUsuario);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            List<Intencion> misIntenciones = intencionService.obtenerIntencionesPorUsuario(usuario);
            
            model.addAttribute("usuario", usuario);
            model.addAttribute("intenciones", misIntenciones);
        }
        
        model.addAttribute("titulo", "Mis Intenciones");
        return "intenciones/mis-intenciones";
    }
}