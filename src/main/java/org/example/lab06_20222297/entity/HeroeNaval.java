package org.example.lab06_20222297.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "heroes_navales")
public class HeroeNaval {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(name = "nombre", nullable = false)
    private String nombre;
    
    @Size(min = 2, max = 50, message = "El rango debe tener entre 2 y 50 caracteres")
    @Column(name = "rango", nullable = true)  // Permitir null temporalmente
    private String rango;
    
    @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_nacimiento", nullable = true)  // Permitir null temporalmente
    private LocalDate fechaNacimiento;
    
    @Size(min = 10, max = 1000, message = "La reseña debe tener entre 10 y 1000 caracteres")
    @Column(name = "resena", nullable = true, columnDefinition = "TEXT")  // Permitir null temporalmente
    private String resena;
    
    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;
    
    // Legacy fields - mantener para compatibilidad con base de datos existente
    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    @Column(name = "descripcion")
    private String descripcion;
    
    @Size(max = 50, message = "El país no puede exceder 50 caracteres")
    @Column(name = "pais")
    private String pais;
    
    // Constructor vacío
    public HeroeNaval() {
        this.fechaCreacion = LocalDate.now();
    }
    
    // Constructor con parámetros nuevos (para el ejercicio)
    public HeroeNaval(String nombre, String rango, LocalDate fechaNacimiento, String resena) {
        this();
        this.nombre = nombre;
        this.rango = rango;
        this.fechaNacimiento = fechaNacimiento;
        this.resena = resena;
    }
    
    // Constructor con parámetros legacy (compatibilidad)
    public HeroeNaval(String nombre, String descripcion, String pais) {
        this();
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.pais = pais;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getRango() {
        return rango;
    }
    
    public void setRango(String rango) {
        this.rango = rango;
    }
    
    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }
    
    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
    
    public String getResena() {
        return resena;
    }
    
    public void setResena(String resena) {
        this.resena = resena;
    }
    
    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    // Legacy getters y setters
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getPais() {
        return pais;
    }
    
    public void setPais(String pais) {
        this.pais = pais;
    }
    
    // Método toString
    @Override
    public String toString() {
        return "HeroeNaval{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", rango='" + rango + '\'' +
                ", fechaNacimiento=" + fechaNacimiento +
                ", resena='" + resena + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                '}';
    }
}