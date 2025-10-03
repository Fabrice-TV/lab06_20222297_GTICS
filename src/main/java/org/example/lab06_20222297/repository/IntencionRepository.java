package org.example.lab06_20222297.repository;

import org.example.lab06_20222297.entity.Intencion;
import org.example.lab06_20222297.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IntencionRepository extends JpaRepository<Intencion, Long> {
    
    /**
     * Busca si existe una intención para un usuario en una sesión específica
     */
    Optional<Intencion> findByUsuarioAndSessionId(Usuario usuario, String sessionId);
    
    /**
     * Verifica si ya existe una intención para una sesión específica
     */
    boolean existsBySessionId(String sessionId);
    
    /**
     * Obtiene todas las intenciones ordenadas por fecha descendente
     */
    @Query("SELECT i FROM Intencion i ORDER BY i.fecha DESC")
    List<Intencion> findAllOrderByFechaDesc();
    
    /**
     * Obtiene las intenciones de un usuario específico ordenadas por fecha
     */
    @Query("SELECT i FROM Intencion i WHERE i.usuario = :usuario ORDER BY i.fecha DESC")
    List<Intencion> findByUsuarioOrderByFechaDesc(@Param("usuario") Usuario usuario);
    
    /**
     * Cuenta el total de intenciones
     */
    @Query("SELECT COUNT(i) FROM Intencion i")
    long countTotal();
}