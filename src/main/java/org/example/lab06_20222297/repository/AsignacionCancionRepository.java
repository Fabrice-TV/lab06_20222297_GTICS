package org.example.lab06_20222297.repository;

import org.example.lab06_20222297.entity.AsignacionCancion;
import org.example.lab06_20222297.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AsignacionCancionRepository extends JpaRepository<AsignacionCancion, Long> {
    
    Optional<AsignacionCancion> findByUsuario(Usuario usuario);
    
    Optional<AsignacionCancion> findByUsuarioId(Long usuarioId);
    
    @Query("SELECT a FROM AsignacionCancion a WHERE a.adivinada = true ORDER BY a.intentos ASC")
    List<AsignacionCancion> findTop10ByAdivinadaTrueOrderByIntentosAsc();
    
    @Query("SELECT a FROM AsignacionCancion a WHERE a.adivinada = true ORDER BY a.intentos ASC LIMIT 10")
    List<AsignacionCancion> findRankingTop10();
    
    List<AsignacionCancion> findByAdivinadaTrue();
    
    @Query("SELECT COUNT(a) FROM AsignacionCancion a WHERE a.adivinada = false")
    Long countPendientes();
    
    @Query("SELECT a FROM AsignacionCancion a JOIN FETCH a.usuario JOIN FETCH a.cancion")
    List<AsignacionCancion> findAllWithUsuarioAndCancion();
}