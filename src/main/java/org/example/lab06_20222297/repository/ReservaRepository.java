package org.example.lab06_20222297.repository;

import org.example.lab06_20222297.entity.Mesa;
import org.example.lab06_20222297.entity.Reserva;
import org.example.lab06_20222297.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    
    // Buscar reserva por usuario
    Optional<Reserva> findByUsuario(Usuario usuario);
    
    // Buscar reserva por mesa
    Optional<Reserva> findByMesa(Mesa mesa);
    
    // Verificar si un usuario ya tiene una reserva
    boolean existsByUsuario(Usuario usuario);
    
    // Verificar si una mesa ya está reservada
    boolean existsByMesa(Mesa mesa);
    
    // Contar total de reservas (mesas ocupadas)
    @Query("SELECT COUNT(r) FROM Reserva r")
    long countReservas();
    
    // Obtener todas las reservas ordenadas por fecha
    @Query("SELECT r FROM Reserva r ORDER BY r.fecha DESC")
    List<Reserva> findAllOrderByFechaDesc();
}