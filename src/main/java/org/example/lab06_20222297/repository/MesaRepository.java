package org.example.lab06_20222297.repository;

import org.example.lab06_20222297.entity.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Long> {
    List<Mesa> findByDisponible(Boolean disponible);
    Optional<Mesa> findByNumero(Integer numero);
    boolean existsByNumero(Integer numero);
}