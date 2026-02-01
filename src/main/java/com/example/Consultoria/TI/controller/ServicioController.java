package com.example.Consultoria.TI.controller;

import org.springframework.ui.Model;
import com.example.Consultoria.TI.modelo.Servicio;
import com.example.Consultoria.TI.service.ServicioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * Sandoval
 */
       @Controller

public class ServicioController {
    @Autowired
    private ServicioService service;

    @GetMapping("/servicios/nuevo")
    public String nuevoServicio(Model model, HttpSession session) {
        // Verificar rol Cliente o Admin
        model.addAttribute("servicio", new Servicio());
        // Cargar listas para dropdowns: tipos, tecnicos
        return "servicios/formulario";
    }

    @PostMapping("/servicios/guardar")
    public String guardar(@ModelAttribute Servicio servicio) {
        service.guardarServicio(servicio); // Incluye detalles/comentarios
        return "redirect:/servicios/lista";
    }

    @PostMapping("/servicios/cambiarEstado/{id}")
    public String cambiarEstado(@PathVariable Long id, @RequestParam String nuevoEstado, @RequestParam String comentario, HttpSession session) {
        // Verificar rol Soporte
        service.cambiarEstado(id, nuevoEstado, comentario);
        return "redirect:/servicios/lista";
    }

    // Rutas para lista, edición, reclamos, presupuestos (imprimir: return view con datos para PDF)
}

