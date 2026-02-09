package com.example.Consultoria.TI.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String home() {
        return "index";  // Apunta a index.html
    }
    
    @GetMapping("/perfil")
    public String perfil() {
        return "usuarios/perfil";  // Puedes crear esta página después
    }
}