
package org.example.lab06_20222297.controller;

import org.example.lab06_20222297.entity.*;
import org.example.lab06_20222297.repository.UsuarioRepository;
import org.example.lab06_20222297.service.JuegoCancionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/juego")
@PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
public class JuegoController {

    @Autowired
    private JuegoCancionService juegoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Página principal del juego
     */
    @GetMapping
    public String juego(Model model, Authentication authentication, HttpSession session) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        System.out.println("DEBUG JUEGO - Usuario: " + usuario.getNombre() + " (ID: " + usuario.getId() + ")");
        
        Optional<AsignacionCancion> asignacionOpt = juegoService.obtenerAsignacionUsuario(usuario);
        System.out.println("DEBUG JUEGO - Asignación encontrada: " + asignacionOpt.isPresent());
        
        if (asignacionOpt.isEmpty()) {
            // No tiene canción asignada, mostrar opción de solicitar
            System.out.println("DEBUG JUEGO - Redirigiendo a sin-asignacion");
            model.addAttribute("sinAsignacion", true);
            return "juego/sin-asignacion";
        }

        AsignacionCancion asignacion = asignacionOpt.get();
        CancionCriolla cancion = asignacion.getCancion();
        System.out.println("DEBUG JUEGO - Canción asignada: " + cancion.getTitulo() + " - " + cancion.getArtista());
        System.out.println("DEBUG JUEGO - Juego completado: " + asignacion.getAdivinada());
        
        // Obtener historial de intentos de la sesión
        @SuppressWarnings("unchecked")
        List<JuegoCancionService.ResultadoIntento> historialIntentos = 
            (List<JuegoCancionService.ResultadoIntento>) session.getAttribute("historialIntentos_" + usuario.getId());
        
        if (historialIntentos == null) {
            historialIntentos = new ArrayList<>();
            session.setAttribute("historialIntentos_" + usuario.getId(), historialIntentos);
        }

        // Crear patrón del título para mostrar espacios y guiones
        String patronTitulo = cancion.getTitulo().replaceAll("[a-zA-ZáéíóúÁÉÍÓÚñÑ]", "?");
        
        model.addAttribute("asignacion", asignacion);
        model.addAttribute("cancion", cancion);
        model.addAttribute("historialIntentos", historialIntentos);
        model.addAttribute("patronTitulo", patronTitulo);
        
        return "juego/adivinar";
    }

    /**
     * Procesar intento de adivinanza
     */
    @PostMapping("/intentar")
    public String procesarIntento(@RequestParam("intento") String intento,
                                 Authentication authentication,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = obtenerUsuarioAutenticado(authentication);
            JuegoCancionService.ResultadoIntento resultado = juegoService.procesarIntento(usuario, intento);
            
            // Agregar al historial en sesión
            @SuppressWarnings("unchecked")
            List<JuegoCancionService.ResultadoIntento> historialIntentos = 
                (List<JuegoCancionService.ResultadoIntento>) session.getAttribute("historialIntentos_" + usuario.getId());
            
            if (historialIntentos == null) {
                historialIntentos = new ArrayList<>();
                session.setAttribute("historialIntentos_" + usuario.getId(), historialIntentos);
            }
            
            historialIntentos.add(resultado);
            
            if (resultado.isAdivinada()) {
                redirectAttributes.addFlashAttribute("mensajeExito", resultado.getMensaje());
                // Limpiar historial al adivinar
                session.removeAttribute("historialIntentos_" + usuario.getId());
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        }
        
        return "redirect:/juego";
    }

    /**
     * Solicitar asignación de canción
     */
    @PostMapping("/solicitar-asignacion")
    public String solicitarAsignacion(@RequestParam(value = "mensaje", required = false) String mensaje,
                                     Authentication authentication,
                                     RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = obtenerUsuarioAutenticado(authentication);
            
            if (mensaje == null || mensaje.trim().isEmpty()) {
                mensaje = "Solicito que me asignen una canción criolla para jugar.";
            }
            
            juegoService.crearSolicitudAsignacion(usuario, mensaje);
            redirectAttributes.addFlashAttribute("mensajeExito", 
                "Tu solicitud ha sido enviada. El administrador la revisará pronto.");
                
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        }
        
        return "redirect:/juego";
    }

    /**
     * Ver ranking de mejores jugadores
     */
    @GetMapping("/ranking")
    public String ranking(Model model) {
        List<AsignacionCancion> ranking = juegoService.obtenerRankingTop10();
        model.addAttribute("ranking", ranking);
        model.addAttribute("titulo", "Ranking Top 10 - Mejores Jugadores");
        return "juego/ranking";
    }

    /**
     * Reiniciar juego (limpiar historial de sesión)
     */
    @PostMapping("/reiniciar")
    public String reiniciarJuego(Authentication authentication, 
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        session.removeAttribute("historialIntentos_" + usuario.getId());
        redirectAttributes.addFlashAttribute("mensajeInfo", "Historial de intentos limpiado");
        return "redirect:/juego";
    }

    /**
     * Método auxiliar para obtener el usuario autenticado
     */
    private Usuario obtenerUsuarioAutenticado(Authentication authentication) {
        String email = authentication.getName();
        return usuarioRepository.findByCorreo(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}
