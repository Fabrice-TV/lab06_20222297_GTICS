package org.example.lab06_20222297.controller;

import org.example.lab06_20222297.entity.Mesa;
import org.example.lab06_20222297.entity.Reserva;
import org.example.lab06_20222297.entity.Usuario;
import org.example.lab06_20222297.repository.MesaRepository;
import org.example.lab06_20222297.repository.ReservaRepository;
import org.example.lab06_20222297.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/oktoberfest/reservas")
@PreAuthorize("hasRole('USUARIO') or hasRole('ADMIN')")
public class OktoberfestController {
    
    @Autowired
    private MesaRepository mesaRepository;
    
    @Autowired
    private ReservaRepository reservaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    // Vista principal - Ver mesas disponibles
    @GetMapping("")
    public String mostrarMesas(Model model, Authentication authentication) {
        
        // Obtener el usuario autenticado
        String correo = authentication.getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);
        
        if (usuarioOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Error al obtener datos del usuario.");
            return "redirect:/dashboard";
        }
        
        Usuario usuario = usuarioOpt.get();
        
        // Obtener todas las mesas
        List<Mesa> todasLasMesas = mesaRepository.findAll();
        List<Mesa> mesasDisponibles = mesaRepository.findByDisponible(true);
        
        // Verificar si el usuario ya tiene una reserva
        Optional<Reserva> reservaExistente = reservaRepository.findByUsuario(usuario);
        
        // Contar mesas ocupadas y libres
        long mesasOcupadas = reservaRepository.countReservas();
        long mesasLibres = todasLasMesas.size() - mesasOcupadas;
        
        model.addAttribute("titulo", "Oktoberfest - Reservar Mesa");
        model.addAttribute("mesasDisponibles", mesasDisponibles);
        model.addAttribute("mesasOcupadas", mesasOcupadas);
        model.addAttribute("mesasLibres", mesasLibres);
        model.addAttribute("totalMesas", todasLasMesas.size());
        model.addAttribute("tieneReserva", reservaExistente.isPresent());
        
        if (reservaExistente.isPresent()) {
            model.addAttribute("reservaActual", reservaExistente.get());
        }
        
        return "oktoberfest/reservar";
    }
    
    // Reservar una mesa
    @PostMapping("/reservar/{mesaId}")
    public String reservarMesa(@PathVariable Long mesaId, 
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        
        // Obtener el usuario autenticado
        String correo = authentication.getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);
        
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al obtener datos del usuario.");
            return "redirect:/oktoberfest/reservas";
        }
        
        Usuario usuario = usuarioOpt.get();
        
        // Verificar si el usuario ya tiene una reserva
        if (reservaRepository.existsByUsuario(usuario)) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Ya tienes una reserva activa. Solo puedes reservar una mesa por cuenta.");
            return "redirect:/oktoberfest/reservas";
        }
        
        // Obtener la mesa
        Optional<Mesa> mesaOpt = mesaRepository.findById(mesaId);
        if (mesaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "La mesa seleccionada no existe.");
            return "redirect:/oktoberfest/reservas";
        }
        
        Mesa mesa = mesaOpt.get();
        
        // Verificar si la mesa está disponible
        if (!mesa.getDisponible()) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "La mesa " + mesa.getNumero() + " ya no está disponible.");
            return "redirect:/oktoberfest/reservas";
        }
        
        // Verificar si la mesa ya está reservada
        if (reservaRepository.existsByMesa(mesa)) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "La mesa " + mesa.getNumero() + " ya está reservada.");
            return "redirect:/oktoberfest/reservas";
        }
        
        try {
            // Crear la reserva
            Reserva nuevaReserva = new Reserva(usuario, mesa);
            reservaRepository.save(nuevaReserva);
            
            // Marcar la mesa como no disponible
            mesa.setDisponible(false);
            mesaRepository.save(mesa);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "¡Reserva exitosa! Has reservado la mesa " + mesa.getNumero() + 
                " con capacidad para " + mesa.getCapacidad() + " personas.");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error al procesar la reserva. Inténtalo nuevamente.");
        }
        
        return "redirect:/oktoberfest/reservas";
    }
    
    // Ver mi reserva
    @GetMapping("/mi-reserva")
    public String verMiReserva(Model model, Authentication authentication, 
                              RedirectAttributes redirectAttributes) {
        
        // Obtener el usuario autenticado
        String correo = authentication.getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);
        
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al obtener datos del usuario.");
            return "redirect:/oktoberfest/reservas";
        }
        
        Usuario usuario = usuarioOpt.get();
        
        // Buscar la reserva del usuario
        Optional<Reserva> reservaOpt = reservaRepository.findByUsuario(usuario);
        
        if (reservaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("infoMessage", 
                "No tienes ninguna reserva activa. ¡Haz tu reserva ahora!");
            return "redirect:/oktoberfest/reservas";
        }
        
        Reserva reserva = reservaOpt.get();
        
        model.addAttribute("titulo", "Mi Reserva - Oktoberfest");
        model.addAttribute("reserva", reserva);
        model.addAttribute("mesa", reserva.getMesa());
        
        return "oktoberfest/mi-reserva";
    }
    
    // Cancelar mi reserva
    @PostMapping("/cancelar-reserva")
    public String cancelarReserva(Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        
        // Obtener el usuario autenticado
        String correo = authentication.getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);
        
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al obtener datos del usuario.");
            return "redirect:/oktoberfest/reservas";
        }
        
        Usuario usuario = usuarioOpt.get();
        
        // Buscar la reserva del usuario
        Optional<Reserva> reservaOpt = reservaRepository.findByUsuario(usuario);
        
        if (reservaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "No tienes ninguna reserva para cancelar.");
            return "redirect:/oktoberfest/reservas";
        }
        
        try {
            Reserva reserva = reservaOpt.get();
            Mesa mesa = reserva.getMesa();
            
            // Eliminar la reserva
            reservaRepository.delete(reserva);
            
            // Marcar la mesa como disponible
            mesa.setDisponible(true);
            mesaRepository.save(mesa);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Tu reserva ha sido cancelada exitosamente. La mesa " + mesa.getNumero() + 
                " ahora está disponible para otros usuarios.");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error al cancelar la reserva. Inténtalo nuevamente.");
        }
        
        return "redirect:/oktoberfest/reservas";
    }
}
