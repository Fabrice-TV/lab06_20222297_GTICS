package org.example.lab06_20222297.service;

import org.example.lab06_20222297.entity.HeroeNaval;
import org.example.lab06_20222297.repository.HeroeNavalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class HeroeNavalService {
    
    @Autowired
    private HeroeNavalRepository heroeNavalRepository;
    
    // Listar todos los héroes
    public List<HeroeNaval> listarTodos() {
        return heroeNavalRepository.findAll();
    }
    
    // Obtener héroe por ID
    public Optional<HeroeNaval> obtenerPorId(Long id) {
        return heroeNavalRepository.findById(id);
    }
    
    // Guardar héroe (crear o actualizar)
    public HeroeNaval guardar(HeroeNaval heroe) {
        if (heroe.getFechaCreacion() == null) {
            heroe.setFechaCreacion(LocalDate.now());
        }
        return heroeNavalRepository.save(heroe);
    }
    
    // Eliminar héroe
    public boolean eliminar(Long id) {
        try {
            if (heroeNavalRepository.existsById(id)) {
                heroeNavalRepository.deleteById(id);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    // Buscar por nombre o rango
    public List<HeroeNaval> buscar(String termino) {
        return heroeNavalRepository.buscarPorNombreORango(termino);
    }
    
    // Listar por rango
    public List<HeroeNaval> listarPorRango(String rango) {
        return heroeNavalRepository.findByRango(rango);
    }
    
    // Listar héroes con datos completos (para el ejercicio)
    public List<HeroeNaval> listarHeroesCompletos() {
        return heroeNavalRepository.findHeroesCompletos();
    }
    
    // Obtener estadísticas
    public long contarTotal() {
        return heroeNavalRepository.count();
    }
    
    // Verificar si existe un héroe
    public boolean existe(Long id) {
        return heroeNavalRepository.existsById(id);
    }
    
    // Métodos para obtener rangos más comunes (para formularios)
    public String[] obtenerRangosComunes() {
        return new String[]{
            "Almirante", "Vicealmirante", "Contralmirante", 
            "Capitán de Navío", "Capitán de Fragata", "Capitán de Corbeta",
            "Teniente Primero", "Teniente Segundo", "Alférez de Navío",
            "Suboficial Mayor", "Suboficial Primero", "Suboficial Segundo"
        };
    }
}