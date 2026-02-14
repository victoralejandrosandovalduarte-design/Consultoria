// HomeController.java
package com.example.Consultoria.TI.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String home() {
        return "index";
    }
    
    // Redirecciones para las rutas que el index.html usa
    @GetMapping("/registro")
    public String redirectRegistro() {
        return "redirect:/usuarios/registro";
    }
    
    @GetMapping("/login")
    public String redirectLogin() {
        return "redirect:/usuarios/login";
    }
    
    // Redirecciones para evitar errores 404 en rutas antiguas
    @GetMapping("/dashboard")
    public String dashboard() {
        return "redirect:/principal";
    }
    
    @GetMapping("/admin")
    public String admin() {
        return "redirect:/principal";
    }
    
    @GetMapping("/soporte")
    public String soporte() {
        return "redirect:/principal";
    }
    
    @GetMapping("/cliente")
    public String cliente() {
        return "redirect:/principal";
    }
    
    // ❌ ELIMINADO: @GetMapping("/principal")
    // Este método ya existe en PrincipalController
    // No lo necesitamos aquí
}