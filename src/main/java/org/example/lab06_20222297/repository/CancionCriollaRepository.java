package org.example.lab06_20222297.repository;

import org.example.lab06_20222297.entity.CancionCriolla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CancionCriollaRepository extends JpaRepository<CancionCriolla, Long> {
    List<CancionCriolla> findByTituloContainingIgnoreCase(String titulo);
    
    List<CancionCriolla> findByActivaTrue();
    
    @Query("SELECT c FROM CancionCriolla c WHERE c.activa = true ORDER BY c.titulo")
    List<CancionCriolla> findActivasOrdenadas();
}