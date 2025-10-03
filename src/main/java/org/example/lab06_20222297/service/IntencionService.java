package org.example.lab06_20222297.service;

import org.example.lab06_20222297.entity.Intencion;
import org.example.lab06_20222297.entity.Usuario;
import org.example.lab06_20222297.repository.IntencionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class IntencionService {
    
    @Autowired
    private IntencionRepository intencionRepository;
    
    // Lista de palabras prohibidas
    private static final List<String> PALABRAS_PROHIBIDAS = Arrays.asList(
        "odio", "pelea", "violencia", "agresi", "golpe", "matar", 
        "muerte", "venganza", "destruir", "dañar", "lastimar", "herir"
    );
    
    /**
     * Valida si el texto contiene palabras prohibidas
     */
    public boolean contienepalabrasProhibidas(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return false;
        }
        
        String textoLowerCase = texto.toLowerCase();
        return PALABRAS_PROHIBIDAS.stream()
                .anyMatch(palabra -> textoLowerCase.contains(palabra.toLowerCase()));
    }
    
    /**
     * Verifica si ya existe una intención para la sesión actual
     */
    public boolean yaExisteIntencionEnSesion(String sessionId) {
        return intencionRepository.existsBySessionId(sessionId);
    }
    
    /**
     * Guarda una nueva intención con validaciones
     */
    public Intencion guardarIntencion(Usuario usuario, String descripcion, String sessionId) {
        // Validar palabras prohibidas
        if (contienepalabrasProhibidas(descripcion)) {
            throw new IllegalArgumentException("La descripción contiene palabras prohibidas");
        }
        
        // Validar que no exista una intención para esta sesión
        if (yaExisteIntencionEnSesion(sessionId)) {
            throw new IllegalArgumentException("Ya existe una intención registrada para esta sesión");
        }
        
        // Crear y guardar la intención
        Intencion intencion = new Intencion(usuario, descripcion, sessionId);
        return intencionRepository.save(intencion);
    }
    
    /**
     * Obtiene todas las intenciones para la vista de administración
     */
    public List<Intencion> obtenerTodasLasIntenciones() {
        return intencionRepository.findAllOrderByFechaDesc();
    }
    
    /**
     * Obtiene las intenciones de un usuario específico
     */
    public List<Intencion> obtenerIntencionesPorUsuario(Usuario usuario) {
        return intencionRepository.findByUsuarioOrderByFechaDesc(usuario);
    }
    
    /**
     * Obtiene una intención por ID
     */
    public Optional<Intencion> obtenerPorId(Long id) {
        return intencionRepository.findById(id);
    }
    
    /**
     * Cuenta el total de intenciones registradas
     */
    public long contarTotal() {
        return intencionRepository.countTotal();
    }
    
    /**
     * Obtiene la intención de un usuario en una sesión específica
     */
    public Optional<Intencion> obtenerIntencionPorUsuarioYSesion(Usuario usuario, String sessionId) {
        return intencionRepository.findByUsuarioAndSessionId(usuario, sessionId);
    }
    
    /**
     * Elimina una intención (solo para admin)
     */
    public boolean eliminarIntencion(Long id) {
        try {
            if (intencionRepository.existsById(id)) {
                intencionRepository.deleteById(id);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}