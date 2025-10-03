package org.example.lab06_20222297.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/juegos")
public class JuegosController {

    @GetMapping("")
    public String mostrarJuegos(Model model) {
        model.addAttribute("titulo", "Juegos del Festival");
        model.addAttribute("descripcion", "Disfruta de nuestros juegos temáticos del mes de octubre");
        return "juegos/index";
    }
    
    @GetMapping("/adivina-cancion")
    public String mostrarAdivinaCancion(Model model) {
        model.addAttribute("titulo", "Adivina la Canción");
        model.addAttribute("descripcion", "Pon a prueba tus conocimientos sobre música criolla");
        return "juegos/adivina-cancion";
    }
    
    @GetMapping("/numero-casa")
    public String mostrarNumeroCasa(Model model) {
        model.addAttribute("titulo", "Número de Casa");
        model.addAttribute("descripcion", "Encuentra el número correcto para conseguir dulces");
        return "juegos/numero-casa";
    }
}