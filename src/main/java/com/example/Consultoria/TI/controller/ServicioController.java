// ServicioController.java
package com.example.Consultoria.TI.controller;

import com.example.Consultoria.TI.modelo.*;
import com.example.Consultoria.TI.repository.ClienteRepository;
import com.example.Consultoria.TI.repository.TecnicoRepository;
import com.example.Consultoria.TI.repository.TipoServicioRepository;
import com.example.Consultoria.TI.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/servicios")
@RequiredArgsConstructor
public class ServicioController {
    
    private final ServicioService servicioService;
    private final ClienteRepository clienteRepository;
    private final TecnicoRepository tecnicoRepository;
    private final TipoServicioRepository tipoServicioRepository;
    
    @GetMapping
    public String listar(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";
        
        model.addAttribute("servicios", servicioService.obtenerServiciosPorRol(usuario));
        model.addAttribute("usuario", usuario);
        return "servicios/lista"; // Se crea vista 
    }
    
    @GetMapping("/nuevo")
    public String nuevoForm(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";
        Servicio servicio = Servicio.builder()
            .estado("PENDIENTE")
            .build();
        
        if ("CLIENTE".equals(usuario.getRol())) {
            servicio.setCliente(usuario.getCliente());
        }
        
        model.addAttribute("servicio", servicio);
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("tecnicos", tecnicoRepository.findAll());
        model.addAttribute("tipos", tipoServicioRepository.findAll());
        
        return "servicios/formulario"; // Se crea esta vista
    }
    
    @PostMapping
    public String guardar(@ModelAttribute Servicio servicio, 
                         HttpSession session,
                         RedirectAttributes redirect) {
        servicioService.save(servicio);
        redirect.addFlashAttribute("exito", "Servicio guardado exitosamente");
        return "redirect:/servicios";
    }
    
    @GetMapping("/{id}")
    public String ver(@PathVariable Long id, Model model, HttpSession session) {
        Servicio servicio = servicioService.findById(id);
        model.addAttribute("servicio", servicio);
        return "servicios/ver";
    }
    
    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id,
                               @RequestParam String estado,
                               @RequestParam(required = false) String comentario,
                               HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || ("CLIENTE".equals(usuario.getRol()))) {
            return "redirect:/principal";
        }
        servicioService.cambiarEstado(id, estado, comentario, usuario);
        return "redirect:/servicios/" + id;
    }
}