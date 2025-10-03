package org.example.lab06_20222297.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FestividadesController {

    @GetMapping("/festividades")
    public String mostrarFestividades(Model model) {
        model.addAttribute("titulo", "Nuestras Festividades");
        model.addAttribute("descripcion", "Descubre las celebraciones más importantes del mes de octubre");
        return "festividades/index";
    }
    
    @GetMapping("/combate-angamos")
    public String mostrarCombateAngamos(Model model) {
        model.addAttribute("titulo", "Combate de Angamos");
        model.addAttribute("fecha", "8 de Octubre");
        return "festividades/combate-angamos-simple";
    }
    
    @GetMapping("/senor-milagros")
    public String mostrarSenorMilagros(Model model) {
        model.addAttribute("titulo", "Señor de los Milagros");
        model.addAttribute("fecha", "Todo Octubre");
        return "festividades/senor-milagros";
    }
    
    @GetMapping("/cancion-criolla")
    public String mostrarCancionCriolla(Model model) {
        model.addAttribute("titulo", "Día de la Canción Criolla");
        model.addAttribute("fecha", "31 de Octubre");
        return "festividades/cancion-criolla";
    }
    
    @GetMapping("/halloween")
    public String mostrarHalloween(Model model) {
        model.addAttribute("titulo", "Halloween");
        model.addAttribute("fecha", "31 de Octubre");
        return "festividades/halloween";
    }
    
    @GetMapping("/oktoberfest")
    public String mostrarOktoberfest(Model model) {
        model.addAttribute("titulo", "Oktoberfest");
        model.addAttribute("fecha", "Todo Octubre");
        return "festividades/oktoberfest";
    }
}