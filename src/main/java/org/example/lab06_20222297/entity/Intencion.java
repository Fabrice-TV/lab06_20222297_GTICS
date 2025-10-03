package org.example.lab06_20222297.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "intenciones")
public class Intencion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 15, max = 255, message = "La descripción debe tener entre 15 y 255 caracteres")
    @Column(name = "descripcion", nullable = false)
    private String descripcion;
    
    @Column(name = "fecha")
    private LocalDateTime fecha;
    
    @Column(name = "session_id", nullable = false)
    private String sessionId;
    
    // Constructores
    public Intencion() {}
    
    public Intencion(Usuario usuario, String descripcion) {
        this.usuario = usuario;
        this.descripcion = descripcion;
        this.fecha = LocalDateTime.now();
    }
    
    public Intencion(Usuario usuario, String descripcion, String sessionId) {
        this.usuario = usuario;
        this.descripcion = descripcion;
        this.sessionId = sessionId;
        this.fecha = LocalDateTime.now();
    }
    
    @PrePersist
    protected void onCreate() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
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
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public LocalDateTime getFecha() {
        return fecha;
    }
    
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}