package org.example.lab06_20222297.service;

import org.example.lab06_20222297.entity.*;
import org.example.lab06_20222297.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

// Servicio para manejar la lógica del juego de canciones
@Service
@Transactional
public class JuegoCancionService {

    @Autowired
    private CancionCriollaRepository cancionRepository;

    @Autowired
    private AsignacionCancionRepository asignacionRepository;

    @Autowired
    private SolicitudAsignacionRepository solicitudRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Obtiene la asignación actual del usuario
     */
    public Optional<AsignacionCancion> obtenerAsignacionUsuario(Usuario usuario) {
        return asignacionRepository.findByUsuario(usuario);
    }

    /**
     * Crea una solicitud de asignación para un usuario
     */
    public SolicitudAsignacion crearSolicitudAsignacion(Usuario usuario, String mensaje) {
        // Verificar si ya tiene una solicitud pendiente
        Optional<SolicitudAsignacion> solicitudExistente = 
            solicitudRepository.findByUsuarioAndEstado(usuario, SolicitudAsignacion.EstadoSolicitud.PENDIENTE);
        
        if (solicitudExistente.isPresent()) {
            throw new RuntimeException("Ya tienes una solicitud pendiente");
        }

        // Verificar si ya tiene una asignación
        if (obtenerAsignacionUsuario(usuario).isPresent()) {
            throw new RuntimeException("Ya tienes una canción asignada");
        }

        SolicitudAsignacion solicitud = new SolicitudAsignacion(usuario, mensaje);
        return solicitudRepository.save(solicitud);
    }

    /**
     * Asigna una canción a un usuario (solo admin)
     */
    public AsignacionCancion asignarCancionAUsuario(Long usuarioId, Long cancionId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        CancionCriolla cancion = cancionRepository.findById(cancionId)
            .orElseThrow(() -> new RuntimeException("Canción no encontrada"));

        // Verificar si ya tiene asignación
        Optional<AsignacionCancion> asignacionExistente = asignacionRepository.findByUsuario(usuario);
        if (asignacionExistente.isPresent()) {
            // Actualizar la asignación existente
            AsignacionCancion asignacion = asignacionExistente.get();
            asignacion.setCancion(cancion);
            asignacion.setIntentos(0);
            asignacion.setAdivinada(false);
            return asignacionRepository.save(asignacion);
        } else {
            // Crear nueva asignación
            AsignacionCancion nuevaAsignacion = new AsignacionCancion(usuario, cancion);
            return asignacionRepository.save(nuevaAsignacion);
        }
    }

    /**
     * Procesa un intento de adivinanza
     */
    public ResultadoIntento procesarIntento(Usuario usuario, String intento) {
        AsignacionCancion asignacion = asignacionRepository.findByUsuario(usuario)
            .orElseThrow(() -> new RuntimeException("No tienes una canción asignada"));

        if (asignacion.getAdivinada()) {
            throw new RuntimeException("Ya adivinaste esta canción");
        }

        String tituloOriginal = asignacion.getCancion().getTitulo().toLowerCase().trim();
        String intentoLimpio = intento.toLowerCase().trim();

        // Incrementar intentos
        asignacion.setIntentos(asignacion.getIntentos() + 1);

        ResultadoIntento resultado = new ResultadoIntento();
        resultado.setIntento(intento);
        resultado.setNumeroIntento(asignacion.getIntentos());

        if (tituloOriginal.equals(intentoLimpio)) {
            // ¡Adivinó!
            asignacion.setAdivinada(true);
            resultado.setAdivinada(true);
            resultado.setMensaje("¡FELICIDADES! ¡Adivinaste en " + asignacion.getIntentos() + " intento(s)!");
        } else {
            // Analizar coincidencias
            resultado = analizarCoincidencias(tituloOriginal, intentoLimpio, resultado);
        }

        asignacionRepository.save(asignacion);
        return resultado;
    }

    /**
     * Analiza las coincidencias entre el intento y el título original
     */
    private ResultadoIntento analizarCoincidencias(String titulo, String intento, ResultadoIntento resultado) {
        char[] tituloChars = titulo.toCharArray();
        char[] intentoChars = intento.toCharArray();
        
        int coincidenciasCorrectas = 0;
        int letrasEnTitulo = 0;
        
        // Contar letras en posición correcta
        int longitudMinima = Math.min(tituloChars.length, intentoChars.length);
        for (int i = 0; i < longitudMinima; i++) {
            if (tituloChars[i] == intentoChars[i]) {
                coincidenciasCorrectas++;
            }
        }
        
        // Contar letras del intento que están en el título (independientemente de la posición)
        for (char c : intentoChars) {
            if (Character.isLetter(c) && titulo.indexOf(c) != -1) {
                letrasEnTitulo++;
            }
        }
        
        resultado.setCoincidenciasCorrectas(coincidenciasCorrectas);
        resultado.setLetrasEnTitulo(letrasEnTitulo);
        resultado.setLongitudIntento(intento.length());
        resultado.setLongitudTitulo(titulo.length());
        
        // Generar patrón de comparación
        StringBuilder patron = new StringBuilder();
        for (int i = 0; i < Math.max(titulo.length(), intento.length()); i++) {
            if (i < titulo.length() && i < intento.length() && titulo.charAt(i) == intento.charAt(i)) {
                patron.append("o"); // Coincidencia
            } else {
                patron.append("x"); // No coincide
            }
        }
        resultado.setPatronCoincidencias(patron.toString());
        
        return resultado;
    }

    /**
     * Obtiene el ranking de los mejores jugadores
     */
    public List<AsignacionCancion> obtenerRankingTop10() {
        return asignacionRepository.findRankingTop10();
    }

    /**
     * Obtiene todas las canciones activas
     */
    public List<CancionCriolla> obtenerCancionesActivas() {
        return cancionRepository.findActivasOrdenadas();
    }

    /**
     * Obtiene todas las solicitudes pendientes
     */
    public List<SolicitudAsignacion> obtenerSolicitudesPendientes() {
        return solicitudRepository.findPendientesOrdenadas();
    }

    /**
     * Obtiene usuarios sin asignación activa
     */
    public List<Usuario> obtenerUsuariosSinAsignacion() {
        List<Usuario> todosUsuarios = usuarioRepository.findByRol_Nombre("USUARIO");
        List<Usuario> usuariosSinAsignacion = new ArrayList<>();
        
        for (Usuario usuario : todosUsuarios) {
            Optional<AsignacionCancion> asignacion = asignacionRepository.findByUsuario(usuario);
            if (!asignacion.isPresent()) {
                usuariosSinAsignacion.add(usuario);
            }
        }
        
        return usuariosSinAsignacion;
    }

    /**
     * Marca una solicitud como procesada
     */
    public void marcarSolicitudProcesada(Long solicitudId) {
        SolicitudAsignacion solicitud = solicitudRepository.findById(solicitudId)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        
        solicitud.setEstado(SolicitudAsignacion.EstadoSolicitud.PROCESADA);
        solicitudRepository.save(solicitud);
    }

    /**
     * Obtiene estadísticas del juego
     */
    public EstadisticasJuego obtenerEstadisticas() {
        EstadisticasJuego stats = new EstadisticasJuego();
        stats.setTotalCanciones(cancionRepository.count());
        stats.setCancionesActivas(cancionRepository.findByActivaTrue().size());
        stats.setTotalAsignaciones(asignacionRepository.count());
        stats.setCancionesAdivinadas(asignacionRepository.findByAdivinadaTrue().size());
        stats.setSolicitudesPendientes(solicitudRepository.countPendientes());
        return stats;
    }

    /**
     * Clase para el resultado de un intento
     */
    public static class ResultadoIntento {
        private String intento;
        private int numeroIntento;
        private boolean adivinada = false;
        private int coincidenciasCorrectas;
        private int letrasEnTitulo;
        private int longitudIntento;
        private int longitudTitulo;
        private String patronCoincidencias;
        private String mensaje;

        // Getters y Setters
        public String getIntento() { return intento; }
        public void setIntento(String intento) { this.intento = intento; }
        
        public int getNumeroIntento() { return numeroIntento; }
        public void setNumeroIntento(int numeroIntento) { this.numeroIntento = numeroIntento; }
        
        public boolean isAdivinada() { return adivinada; }
        public void setAdivinada(boolean adivinada) { this.adivinada = adivinada; }
        
        public int getCoincidenciasCorrectas() { return coincidenciasCorrectas; }
        public void setCoincidenciasCorrectas(int coincidenciasCorrectas) { this.coincidenciasCorrectas = coincidenciasCorrectas; }
        
        public int getLetrasEnTitulo() { return letrasEnTitulo; }
        public void setLetrasEnTitulo(int letrasEnTitulo) { this.letrasEnTitulo = letrasEnTitulo; }
        
        public int getLongitudIntento() { return longitudIntento; }
        public void setLongitudIntento(int longitudIntento) { this.longitudIntento = longitudIntento; }
        
        public int getLongitudTitulo() { return longitudTitulo; }
        public void setLongitudTitulo(int longitudTitulo) { this.longitudTitulo = longitudTitulo; }
        
        public String getPatronCoincidencias() { return patronCoincidencias; }
        public void setPatronCoincidencias(String patronCoincidencias) { this.patronCoincidencias = patronCoincidencias; }
        
        public String getMensaje() { return mensaje; }
        public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    }

    /**
     * Clase para estadísticas del juego
     */
    public static class EstadisticasJuego {
        private long totalCanciones;
        private long cancionesActivas;
        private long totalAsignaciones;
        private long cancionesAdivinadas;
        private long solicitudesPendientes;

        // Getters y Setters
        public long getTotalCanciones() { return totalCanciones; }
        public void setTotalCanciones(long totalCanciones) { this.totalCanciones = totalCanciones; }
        
        public long getCancionesActivas() { return cancionesActivas; }
        public void setCancionesActivas(long cancionesActivas) { this.cancionesActivas = cancionesActivas; }
        
        public long getTotalAsignaciones() { return totalAsignaciones; }
        public void setTotalAsignaciones(long totalAsignaciones) { this.totalAsignaciones = totalAsignaciones; }
        
        public long getCancionesAdivinadas() { return cancionesAdivinadas; }
        public void setCancionesAdivinadas(long cancionesAdivinadas) { this.cancionesAdivinadas = cancionesAdivinadas; }
        
        public long getSolicitudesPendientes() { return solicitudesPendientes; }
        public void setSolicitudesPendientes(long solicitudesPendientes) { this.solicitudesPendientes = solicitudesPendientes; }
    }
}