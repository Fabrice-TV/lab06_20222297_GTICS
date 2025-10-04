package org.example.lab06_20222297.controller;

import org.example.lab06_20222297.entity.*;
import org.example.lab06_20222297.repository.AsignacionCancionRepository;
import org.example.lab06_20222297.repository.CancionCriollaRepository;
import org.example.lab06_20222297.repository.SolicitudAsignacionRepository;
import org.example.lab06_20222297.repository.UsuarioRepository;
import org.example.lab06_20222297.service.JuegoCancionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

// Controlador para administración del juego de canciones
@Controller
@RequestMapping("/admin/juego")
@PreAuthorize("hasRole('ADMIN')")
public class AdminJuegoController {

    @Autowired
    private JuegoCancionService juegoService;

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private CancionCriollaRepository cancionCriollaRepository;
    
    @Autowired
    private AsignacionCancionRepository asignacionRepository;
    
    @Autowired
    private SolicitudAsignacionRepository solicitudRepository;

    /**
     * Dashboard del admin para el juego
     */
    @GetMapping
    public String adminDashboard(Model model) {
        JuegoCancionService.EstadisticasJuego stats = juegoService.obtenerEstadisticas();
        List<SolicitudAsignacion> solicitudesPendientes = juegoService.obtenerSolicitudesPendientes();
        
        model.addAttribute("estadisticas", stats);
        model.addAttribute("solicitudesPendientes", solicitudesPendientes);
        model.addAttribute("titulo", "Administración del Juego - Canción Criolla");
        
        return "admin/juego-dashboard";
    }

    /**
     * Endpoint temporal para insertar canciones de prueba
     */
    @GetMapping("/insertar-canciones-prueba")
    @ResponseBody
    public String insertarCancionesPrueba() {
        try {
            // Solo insertar si no hay canciones
            if (cancionCriollaRepository.count() == 0) {
                CancionCriolla cancion1 = new CancionCriolla();
                cancion1.setTitulo("La Flor de la Canela");
                cancion1.setArtista("Chabuca Granda");
                cancion1.setActiva(true);
                cancionCriollaRepository.save(cancion1);
                
                CancionCriolla cancion2 = new CancionCriolla();
                cancion2.setTitulo("El Cóndor Pasa");
                cancion2.setArtista("Daniel Alomía Robles");
                cancion2.setActiva(true);
                cancionCriollaRepository.save(cancion2);
                
                CancionCriolla cancion3 = new CancionCriolla();
                cancion3.setTitulo("José Antonio");
                cancion3.setArtista("Chabuca Granda");
                cancion3.setActiva(true);
                cancionCriollaRepository.save(cancion3);
                
                return "Se insertaron 3 canciones de prueba exitosamente";
            } else {
                return "Ya existen " + cancionCriollaRepository.count() + " canciones en la base de datos";
            }
        } catch (Exception e) {
            return "Error al insertar canciones: " + e.getMessage();
        }
    }

    /**
     * Vista para asignar canciones a usuarios
     */
    @GetMapping("/asignar")
    public String mostrarAsignaciones(Model model) {
        // Obtener todos los usuarios directamente del repositorio
        List<Usuario> todosUsuarios = usuarioRepository.findAll();
        List<CancionCriolla> canciones = cancionCriollaRepository.findAll();
        List<AsignacionCancion> asignaciones = asignacionRepository.findAll();
        
        // Filtrar usuarios que no sean ADMIN
        List<Usuario> usuariosSinAsignacion = todosUsuarios.stream()
            .filter(u -> u.getRol() != null && !"ADMIN".equals(u.getRol().getNombre()))
            .toList();
        
        // Debug: mostrar conteos
        System.out.println("Total usuarios en BD: " + todosUsuarios.size());
        System.out.println("Usuarios no admin: " + usuariosSinAsignacion.size());
        System.out.println("Canciones criollas: " + canciones.size());
        System.out.println("Total asignaciones: " + asignaciones.size());
        
        model.addAttribute("usuariosSinAsignacion", usuariosSinAsignacion);
        model.addAttribute("canciones", canciones);
        model.addAttribute("asignaciones", asignaciones);
        model.addAttribute("titulo", "Asignar Canciones a Usuarios");
        
        return "admin/asignar-canciones";
    }

    /**
     * Procesar asignación de canción
     */
    @PostMapping("/asignar")
    public String asignarCancion(@RequestParam("usuarioId") Long usuarioId,
                                @RequestParam("cancionId") Long cancionId,
                                RedirectAttributes redirectAttributes) {
        try {
            AsignacionCancion asignacion = juegoService.asignarCancionAUsuario(usuarioId, cancionId);
            redirectAttributes.addFlashAttribute("mensajeExito", 
                "Canción asignada exitosamente a " + asignacion.getUsuario().getNombre());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        }
        
        return "redirect:/admin/juego/asignar";
    }

    /**
     * Ver todas las asignaciones actuales
     */
    @GetMapping("/asignaciones")
    public String verAsignaciones(Model model) {
        // Implementar método en el servicio para obtener todas las asignaciones
        model.addAttribute("titulo", "Todas las Asignaciones");
        return "admin/ver-asignaciones";
    }

    /**
     * Gestionar solicitudes de asignación
     */
    @GetMapping("/solicitudes")
    public String gestionarSolicitudes(Model model) {
        List<SolicitudAsignacion> solicitudesPendientes = juegoService.obtenerSolicitudesPendientes();
        List<CancionCriolla> canciones = juegoService.obtenerCancionesActivas();
        
        model.addAttribute("solicitudes", solicitudesPendientes);
        model.addAttribute("canciones", canciones);
        model.addAttribute("titulo", "Gestionar Solicitudes de Asignación");
        
        return "admin/gestionar-solicitudes";
    }

    /**
     * Procesar solicitud (asignar canción desde solicitud)
     */
    @PostMapping("/procesar-solicitud")
    public String procesarSolicitud(@RequestParam("solicitudId") Long solicitudId,
                                   @RequestParam("cancionId") Long cancionId,
                                   @RequestParam("accion") String accion,
                                   RedirectAttributes redirectAttributes) {
        try {
            if ("asignar".equals(accion)) {
                // Obtener la solicitud para conseguir el usuario
                SolicitudAsignacion solicitud = juegoService.obtenerSolicitudesPendientes().stream()
                    .filter(s -> s.getId().equals(solicitudId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
                
                // Asignar canción
                juegoService.asignarCancionAUsuario(solicitud.getUsuario().getId(), cancionId);
                
                // Marcar solicitud como procesada
                juegoService.marcarSolicitudProcesada(solicitudId);
                
                redirectAttributes.addFlashAttribute("mensajeExito", 
                    "Canción asignada y solicitud procesada exitosamente");
            } else if ("rechazar".equals(accion)) {
                // Solo marcar como procesada sin asignar
                juegoService.marcarSolicitudProcesada(solicitudId);
                redirectAttributes.addFlashAttribute("mensajeInfo", "Solicitud rechazada");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        }
        
        return "redirect:/admin/juego/solicitudes";
    }

    /**
     * Ver ranking (acceso admin)
     */
    @GetMapping("/ranking")
    public String verRanking(Model model) {
        List<AsignacionCancion> ranking = juegoService.obtenerRankingTop10();
        model.addAttribute("ranking", ranking);
        model.addAttribute("titulo", "Ranking Top 10 - Vista Administrador");
        model.addAttribute("esAdmin", true);
        return "juego/ranking";
    }

    /**
     * Estadísticas detalladas
     */
    @GetMapping("/estadisticas")
    public String estadisticasDetalladas(Model model) {
        JuegoCancionService.EstadisticasJuego stats = juegoService.obtenerEstadisticas();
        model.addAttribute("estadisticas", stats);
        model.addAttribute("titulo", "Estadísticas Detalladas del Juego");
        return "admin/estadisticas-juego";
    }
}