package org.example.lab06_20222297.repository;

import org.example.lab06_20222297.entity.SolicitudAsignacion;
import org.example.lab06_20222297.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitudAsignacionRepository extends JpaRepository<SolicitudAsignacion, Long> {
    
    @Query("SELECT s FROM SolicitudAsignacion s WHERE s.estado = 'PENDIENTE' ORDER BY s.fechaSolicitud ASC")
    List<SolicitudAsignacion> findPendientesOrdenadas();
    
    List<SolicitudAsignacion> findByUsuario(Usuario usuario);
    
    Optional<SolicitudAsignacion> findByUsuarioAndEstado(Usuario usuario, SolicitudAsignacion.EstadoSolicitud estado);
    
    @Query("SELECT COUNT(s) FROM SolicitudAsignacion s WHERE s.estado = 'PENDIENTE'")
    Long countPendientes();
    
    @Query("SELECT s FROM SolicitudAsignacion s JOIN FETCH s.usuario ORDER BY s.fechaSolicitud DESC")
    List<SolicitudAsignacion> findAllWithUsuario();
}