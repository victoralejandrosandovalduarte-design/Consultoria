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

    @GetMapping("/registro")
    public String redirectRegistro() {
        return "redirect:/usuarios/registro";
    }

    @GetMapping("/login")
    public String redirectLogin() {
        return "redirect:/usuarios/login";
    }

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
}
