package org.example.lab06_20222297.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes_asignacion")
public class SolicitudAsignacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud;
    
    @Column(name = "estado")
    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;
    
    @Column(name = "mensaje")
    private String mensaje;
    
    public enum EstadoSolicitud {
        PENDIENTE, PROCESADA, RECHAZADA
    }
    
    // Constructores
    public SolicitudAsignacion() {
        this.fechaSolicitud = LocalDateTime.now();
    }
    
    public SolicitudAsignacion(Usuario usuario, String mensaje) {
        this();
        this.usuario = usuario;
        this.mensaje = mensaje;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Usuario getUsuario() {
        return usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }
    
    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }
    
    public EstadoSolicitud getEstado() {
        return estado;
    }
    
    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }
    
    public String getMensaje() {
        return mensaje;
    }
    
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}