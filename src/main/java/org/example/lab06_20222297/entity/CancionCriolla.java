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
    
    @Size(max = 100, message = "El artista no puede tener más de 100 caracteres")
    private String artista;
    
    @Column(name = "numero_letras")
    private Integer numeroLetras;
    
    @Column(name = "numero_espacios")
    private Integer numeroEspacios;
    
    @Column(name = "patron_guiones")
    private String patronGuiones;
    
    @Column(name = "activa")
    private Boolean activa = true;
    
    // Constructores
    public CancionCriolla() {}
    
    public CancionCriolla(String titulo, String letra) {
        this.titulo = titulo;
        this.letra = letra;
        this.calcularPropiedades();
    }

    // Método para calcular propiedades automáticamente
    @PrePersist
    @PreUpdate
    public void calcularPropiedades() {
        if (titulo != null) {
            // Contar letras (solo caracteres alfabéticos)
            this.numeroLetras = (int) titulo.chars().filter(Character::isLetter).count();
            
            // Contar espacios
            this.numeroEspacios = (int) titulo.chars().filter(c -> c == ' ').count();
            
            // Generar patrón de guiones
            this.patronGuiones = titulo.replaceAll("[a-zA-ZáéíóúÁÉÍÓÚñÑ]", "-");
        }
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
        this.calcularPropiedades();
    }
    
    public String getLetra() {
        return letra;
    }
    
    public void setLetra(String letra) {
        this.letra = letra;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public Integer getNumeroLetras() {
        return numeroLetras;
    }

    public void setNumeroLetras(Integer numeroLetras) {
        this.numeroLetras = numeroLetras;
    }

    public Integer getNumeroEspacios() {
        return numeroEspacios;
    }

    public void setNumeroEspacios(Integer numeroEspacios) {
        this.numeroEspacios = numeroEspacios;
    }

    public String getPatronGuiones() {
        return patronGuiones;
    }

    public void setPatronGuiones(String patronGuiones) {
        this.patronGuiones = patronGuiones;
    }

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }

    @Override
    public String toString() {
        return titulo + (artista != null ? " - " + artista : "");
    }
}