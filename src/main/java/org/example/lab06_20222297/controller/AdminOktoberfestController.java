package org.example.lab06_20222297.controller;

import org.example.lab06_20222297.entity.Mesa;
import org.example.lab06_20222297.entity.Reserva;
import org.example.lab06_20222297.repository.MesaRepository;
import org.example.lab06_20222297.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/oktoberfest")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOktoberfestController {
    
    @Autowired
    private MesaRepository mesaRepository;
    
    @Autowired
    private ReservaRepository reservaRepository;
    
    // Vista principal - Ver todas las mesas y reservas
    @GetMapping("")
    public String mostrarReservas(Model model) {
        
        List<Mesa> todasLasMesas = mesaRepository.findAll();
        List<Reserva> todasLasReservas = reservaRepository.findAllOrderByFechaDesc();
        
        // Contar mesas ocupadas y libres
        long mesasOcupadas = reservaRepository.countReservas();
        long mesasLibres = todasLasMesas.size() - mesasOcupadas;
        
        model.addAttribute("titulo", "Administrar Oktoberfest - Reservas");
        model.addAttribute("mesas", todasLasMesas);
        model.addAttribute("reservas", todasLasReservas);
        model.addAttribute("mesasOcupadas", mesasOcupadas);
        model.addAttribute("mesasLibres", mesasLibres);
        model.addAttribute("totalMesas", todasLasMesas.size());
        
        return "admin/oktoberfest-reservas";
    }
    
    // Liberar una mesa (cancelar reserva)
    @PostMapping("/liberar-mesa/{reservaId}")
    public String liberarMesa(@PathVariable Long reservaId,
                             RedirectAttributes redirectAttributes) {
        
        Optional<Reserva> reservaOpt = reservaRepository.findById(reservaId);
        
        if (reservaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "La reserva no existe.");
            return "redirect:/admin/oktoberfest";
        }
        
        try {
            Reserva reserva = reservaOpt.get();
            Mesa mesa = reserva.getMesa();
            String nombreUsuario = reserva.getUsuario().getNombre();
            
            // Eliminar la reserva
            reservaRepository.delete(reserva);
            
            // Marcar la mesa como disponible
            mesa.setDisponible(true);
            mesaRepository.save(mesa);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Mesa " + mesa.getNumero() + " liberada exitosamente. " +
                "La reserva de " + nombreUsuario + " ha sido cancelada.");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error al liberar la mesa. Inténtalo nuevamente.");
        }
        
        return "redirect:/admin/oktoberfest";
    }
    
    // Formulario para reasignar capacidad de mesa
    @GetMapping("/reasignar-capacidad")
    public String mostrarFormularioReasignar(Model model) {
        
        List<Mesa> todasLasMesas = mesaRepository.findAll();
        
        model.addAttribute("titulo", "Reasignar Capacidad de Mesas");
        model.addAttribute("mesas", todasLasMesas);
        
        return "admin/reasignar-capacidad";
    }
    
    // Procesar reasignación de capacidad
    @PostMapping("/reasignar-capacidad/{mesaId}")
    public String procesarReasignacion(@PathVariable Long mesaId,
                                     @RequestParam("nuevaCapacidad") Integer nuevaCapacidad,
                                     RedirectAttributes redirectAttributes) {
        
        if (nuevaCapacidad == null || nuevaCapacidad < 1 || nuevaCapacidad > 4) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "La capacidad debe estar entre 1 y 4 personas.");
            return "redirect:/admin/oktoberfest/reasignar-capacidad";
        }
        
        Optional<Mesa> mesaOpt = mesaRepository.findById(mesaId);
        
        if (mesaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "La mesa no existe.");
            return "redirect:/admin/oktoberfest/reasignar-capacidad";
        }
        
        try {
            Mesa mesa = mesaOpt.get();
            Integer capacidadAnterior = mesa.getCapacidad();
            
            mesa.setCapacidad(nuevaCapacidad);
            mesaRepository.save(mesa);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Capacidad de mesa " + mesa.getNumero() + " actualizada de " + 
                capacidadAnterior + " a " + nuevaCapacidad + " personas.");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error al actualizar la capacidad. Inténtalo nuevamente.");
        }
        
        return "redirect:/admin/oktoberfest/reasignar-capacidad";
    }
    
    // Crear nueva mesa
    @GetMapping("/nueva-mesa")
    public String mostrarFormularioNuevaMesa(Model model) {
        
        model.addAttribute("titulo", "Crear Nueva Mesa");
        model.addAttribute("mesa", new Mesa());
        
        return "admin/oktoberfest";
    }
    
    // Procesar nueva mesa
    @PostMapping("/nueva-mesa")
    public String procesarNuevaMesa(@Valid @ModelAttribute Mesa mesa,
                                   BindingResult result,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("titulo", "Crear Nueva Mesa");
            model.addAttribute("errorMessage", "Por favor corrige los errores en el formulario.");
            return "admin/oktoberfest";
        }
        
        // Validar que la capacidad esté entre 1 y 4
        if (mesa.getCapacidad() == null || mesa.getCapacidad() < 1 || mesa.getCapacidad() > 4) {
            model.addAttribute("titulo", "Crear Nueva Mesa");
            model.addAttribute("errorMessage", "La capacidad debe estar entre 1 y 4 personas.");
            return "admin/oktoberfest";
        }
        
        // Verificar que el número de mesa no exista
        if (mesaRepository.existsByNumero(mesa.getNumero())) {
            model.addAttribute("titulo", "Crear Nueva Mesa");
            model.addAttribute("errorMessage", "Ya existe una mesa con el número " + mesa.getNumero() + ".");
            return "admin/oktoberfest";
        }
        
        try {
            mesa.setDisponible(true); // Nueva mesa siempre disponible
            mesaRepository.save(mesa);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Mesa " + mesa.getNumero() + " creada exitosamente con capacidad para " + 
                mesa.getCapacidad() + " personas.");
            
        } catch (Exception e) {
            model.addAttribute("titulo", "Crear Nueva Mesa");
            model.addAttribute("errorMessage", "Error al crear la mesa. Inténtalo nuevamente.");
            return "admin/oktoberfest";
        }
        
        return "redirect:/admin/oktoberfest";
    }
    
    // Eliminar mesa (solo si no tiene reserva)
    @PostMapping("/eliminar-mesa/{mesaId}")
    public String eliminarMesa(@PathVariable Long mesaId,
                              RedirectAttributes redirectAttributes) {
        
        Optional<Mesa> mesaOpt = mesaRepository.findById(mesaId);
        
        if (mesaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "La mesa no existe.");
            return "redirect:/admin/oktoberfest";
        }
        
        Mesa mesa = mesaOpt.get();
        
        // Verificar si la mesa tiene una reserva activa
        if (reservaRepository.existsByMesa(mesa)) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "No se puede eliminar la mesa " + mesa.getNumero() + 
                " porque tiene una reserva activa. Libera la mesa primero.");
            return "redirect:/admin/oktoberfest";
        }
        
        try {
            mesaRepository.delete(mesa);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Mesa " + mesa.getNumero() + " eliminada exitosamente.");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error al eliminar la mesa. Inténtalo nuevamente.");
        }
        
        return "redirect:/admin/oktoberfest";
    }
}