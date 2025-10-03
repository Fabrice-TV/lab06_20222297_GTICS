package org.example.lab06_20222297.repository;

import org.example.lab06_20222297.entity.HeroeNaval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HeroeNavalRepository extends JpaRepository<HeroeNaval, Long> {
    

    List<HeroeNaval> findByPais(String pais);
    List<HeroeNaval> findByNombreContainingIgnoreCase(String nombre);
    
    // Verificar existencia por nombre
    boolean existsByNombre(String nombre);
    
    // Nuevos métodos para el ejercicio
    List<HeroeNaval> findByRango(String rango);
    
    List<HeroeNaval> findByFechaNacimientoBetween(LocalDate fechaInicio, LocalDate fechaFin);
    
    @Query("SELECT h FROM HeroeNaval h WHERE h.nombre IS NOT NULL ORDER BY h.fechaCreacion DESC")
    List<HeroeNaval> findHeroesCompletos();
    
    @Query("SELECT h FROM HeroeNaval h WHERE h.nombre LIKE %?1% OR h.rango LIKE %?1%")
    List<HeroeNaval> buscarPorNombreORango(String termino);
    
    // Ordenar por fecha de creación descendente
    List<HeroeNaval> findAllByOrderByFechaCreacionDesc();
}