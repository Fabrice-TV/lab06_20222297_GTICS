package org.example.lab06_20222297.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "asignaciones_cancion")
public class AsignacionCancion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cancion_id", nullable = false)
    private CancionCriolla cancion;
    
    @Column(name = "intentos")
    private Integer intentos = 0;
    
    @Column(name = "adivinada")
    private Boolean adivinada = false;
    
    // Constructores
    public AsignacionCancion() {}
    
    public AsignacionCancion(Usuario usuario, CancionCriolla cancion) {
        this.usuario = usuario;
        this.cancion = cancion;
        this.intentos = 0;
        this.adivinada = false;
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
    
    public CancionCriolla getCancion() {
        return cancion;
    }
    
    public void setCancion(CancionCriolla cancion) {
        this.cancion = cancion;
    }
    
    public Integer getIntentos() {
        return intentos;
    }
    
    public void setIntentos(Integer intentos) {
        this.intentos = intentos;
    }
    
    public Boolean getAdivinada() {
        return adivinada;
    }
    
    public void setAdivinada(Boolean adivinada) {
        this.adivinada = adivinada;
    }
}