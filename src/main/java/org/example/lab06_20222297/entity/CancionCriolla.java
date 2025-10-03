package org.example.lab06_20222297.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "canciones_criollas")
public class CancionCriolla {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150, message = "El título no puede exceder 150 caracteres")
    @Column(name = "titulo", nullable = false)
    private String titulo;
    
    @Lob
    @Column(name = "letra", columnDefinition = "TEXT")
    private String letra;
    
    // Constructores
    public CancionCriolla() {}
    
    public CancionCriolla(String titulo, String letra) {
        this.titulo = titulo;
        this.letra = letra;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public String getLetra() {
        return letra;
    }
    
    public void setLetra(String letra) {
        this.letra = letra;
    }
}