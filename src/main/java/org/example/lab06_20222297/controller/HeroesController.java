package org.example.lab06_20222297.controller;

import org.example.lab06_20222297.entity.HeroeNaval;
import org.example.lab06_20222297.service.HeroeNavalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/heroes")
public class HeroesController {
    
    @Autowired
    private HeroeNavalService heroeNavalService;
    
    // VISTA PÚBLICA - Todos pueden ver (Visitantes, Usuarios, Admin)
    @GetMapping("")
    public String listarHeroes(Model model, 
                               @RequestParam(value = "buscar", required = false) String termino) {
        
        model.addAttribute("titulo", "Héroes Navales - Festival de Octubre");
        
        if (termino != null && !termino.trim().isEmpty()) {
            model.addAttribute("heroes", heroeNavalService.buscar(termino.trim()));
            model.addAttribute("terminoBusqueda", termino);
        } else {
            model.addAttribute("heroes", heroeNavalService.listarTodos());
        }
        
        model.addAttribute("totalHeroes", heroeNavalService.contarTotal());
        
        return "heroes/lista";
    }
    
    // VISTA DETALLE - Todos pueden ver
    @GetMapping("/{id}")
    public String verDetalle(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        
        Optional<HeroeNaval> heroeOpt = heroeNavalService.obtenerPorId(id);
        
        if (heroeOpt.isPresent()) {
            model.addAttribute("heroe", heroeOpt.get());
            model.addAttribute("titulo", "Detalle de " + heroeOpt.get().getNombre());
            return "heroes/detalle";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "El héroe naval no fue encontrado.");
            return "redirect:/heroes";
        }
    }
    
    // FORMULARIO NUEVO - Solo ADMIN
    @GetMapping("/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("titulo", "Registrar Nuevo Héroe Naval");
        model.addAttribute("heroe", new HeroeNaval());
        model.addAttribute("rangos", heroeNavalService.obtenerRangosComunes());
        model.addAttribute("esNuevo", true);
        return "heroes/formulario";
    }
    
    // PROCESAR NUEVO - Solo ADMIN
    @PostMapping("/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public String procesarNuevo(@Valid @ModelAttribute HeroeNaval heroe,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("titulo", "Registrar Nuevo Héroe Naval");
            model.addAttribute("rangos", heroeNavalService.obtenerRangosComunes());
            model.addAttribute("esNuevo", true);
            model.addAttribute("errorMessage", "Por favor corrige los errores en el formulario.");
            return "heroes/formulario";
        }
        
        try {
            HeroeNaval heroeGuardado = heroeNavalService.guardar(heroe);
            redirectAttributes.addFlashAttribute("successMessage", 
                "¡Héroe naval '" + heroeGuardado.getNombre() + "' registrado exitosamente!");
            return "redirect:/heroes";
        } catch (Exception e) {
            model.addAttribute("titulo", "Registrar Nuevo Héroe Naval");
            model.addAttribute("rangos", heroeNavalService.obtenerRangosComunes());
            model.addAttribute("esNuevo", true);
            model.addAttribute("errorMessage", "Error al guardar el héroe. Intenta nuevamente.");
            return "heroes/formulario";
        }
    }
    
    // FORMULARIO EDITAR - Solo ADMIN
    @GetMapping("/{id}/editar")
    @PreAuthorize("hasRole('ADMIN')")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        
        Optional<HeroeNaval> heroeOpt = heroeNavalService.obtenerPorId(id);
        
        if (heroeOpt.isPresent()) {
            model.addAttribute("titulo", "Editar Héroe Naval");
            model.addAttribute("heroe", heroeOpt.get());
            model.addAttribute("rangos", heroeNavalService.obtenerRangosComunes());
            model.addAttribute("esNuevo", false);
            return "heroes/formulario";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "El héroe naval no fue encontrado.");
            return "redirect:/heroes";
        }
    }
    
    // PROCESAR EDITAR - Solo ADMIN
    @PostMapping("/{id}/editar")
    @PreAuthorize("hasRole('ADMIN')")
    public String procesarEditar(@PathVariable Long id,
                                @Valid @ModelAttribute HeroeNaval heroe,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("titulo", "Editar Héroe Naval");
            model.addAttribute("rangos", heroeNavalService.obtenerRangosComunes());
            model.addAttribute("esNuevo", false);
            model.addAttribute("errorMessage", "Por favor corrige los errores en el formulario.");
            return "heroes/formulario";
        }
        
        try {
            heroe.setId(id); // Asegurar que mantenga el ID
            HeroeNaval heroeActualizado = heroeNavalService.guardar(heroe);
            redirectAttributes.addFlashAttribute("successMessage", 
                "¡Héroe naval '" + heroeActualizado.getNombre() + "' actualizado exitosamente!");
            return "redirect:/heroes";
        } catch (Exception e) {
            model.addAttribute("titulo", "Editar Héroe Naval");
            model.addAttribute("rangos", heroeNavalService.obtenerRangosComunes());
            model.addAttribute("esNuevo", false);
            model.addAttribute("errorMessage", "Error al actualizar el héroe. Intenta nuevamente.");
            return "heroes/formulario";
        }
    }
    
    // ELIMINAR - Solo ADMIN
    @PostMapping("/{id}/eliminar")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        
        Optional<HeroeNaval> heroeOpt = heroeNavalService.obtenerPorId(id);
        
        if (heroeOpt.isPresent()) {
            String nombreHeroe = heroeOpt.get().getNombre();
            
            if (heroeNavalService.eliminar(id)) {
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Héroe naval '" + nombreHeroe + "' eliminado exitosamente.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Error al eliminar el héroe naval.");
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "El héroe naval no fue encontrado.");
        }
        
        return "redirect:/heroes";
    }
}