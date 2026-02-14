// ServicioController.java - Actualizado para manejar creación por roles y visualización en grilla
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

import java.time.LocalDateTime;
import java.util.List;

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
        if (usuario == null) return "redirect:/usuarios/login";
        
        List<Servicio> servicios;
        if ("ADMIN".equals(usuario.getRol())) {
            servicios = servicioService.findAll();
        } else if ("SOPORTE".equals(usuario.getRol())) {
            Tecnico tecnico = (Tecnico) session.getAttribute("tecnico");
            servicios = servicioService.obtenerServiciosPorTecnico(tecnico.getIdTecnico());
        } else {
            servicios = servicioService.obtenerServiciosPorCliente(usuario.getCliente().getIdCliente());
        }
        
        model.addAttribute("servicios", servicios);
        model.addAttribute("usuario", usuario);
        return "servicios/lista";
    }
    
    @GetMapping("/nuevo")
    public String nuevoForm(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/usuarios/login";
        
        Servicio servicio = Servicio.builder()
            .estado("PENDIENTE")
            .fechaHoraAgendamiento(LocalDateTime.now())
            .build();
        
        if ("CLIENTE".equals(usuario.getRol())) {
            servicio.setCliente(usuario.getCliente());
        }
        
        model.addAttribute("servicio", servicio);
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("tecnicos", tecnicoRepository.findAll());
        model.addAttribute("tipos", tipoServicioRepository.findAll());
        model.addAttribute("esAdminOSoporte", "ADMIN".equals(usuario.getRol()) || "SOPORTE".equals(usuario.getRol()));
        
        return "servicios/formulario";
    }
    
    @PostMapping
    public String guardar(@ModelAttribute Servicio servicio, 
                         HttpSession session,
                         RedirectAttributes redirect) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/usuarios/login";
        
        // Validaciones por rol
        if ("CLIENTE".equals(usuario.getRol()) && !servicio.getCliente().getIdCliente().equals(usuario.getCliente().getIdCliente())) {
            redirect.addFlashAttribute("error", "No autorizado");
            return "redirect:/servicios";
        }
        
        servicioService.save(servicio);
        redirect.addFlashAttribute("exito", "Servicio guardado exitosamente");
        return "redirect:/servicios";
    }
    
    @GetMapping("/{id}")
    public String ver(@PathVariable Long id, Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/usuarios/login";
        
        Servicio servicio = servicioService.findById(id);
        
        // Verificar acceso
        boolean accesoPermitido = false;
        if ("ADMIN".equals(usuario.getRol())) {
            accesoPermitido = true;
        } else if ("SOPORTE".equals(usuario.getRol())) {
            Tecnico tecnico = (Tecnico) session.getAttribute("tecnico");
            if (servicio.getTecnicoAsignado() != null && servicio.getTecnicoAsignado().getIdTecnico().equals(tecnico.getIdTecnico())) {
                accesoPermitido = true;
            }
        } else if ("CLIENTE".equals(usuario.getRol())) {
            if (servicio.getCliente().getIdCliente().equals(usuario.getCliente().getIdCliente())) {
                accesoPermitido = true;
            }
        }
        
        if (!accesoPermitido) {
            return "redirect:/servicios";
        }
        
        model.addAttribute("servicio", servicio);
        model.addAttribute("usuario", usuario);
        return "servicios/ver";
    }
    
    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id,
                               @RequestParam String estado,
                               @RequestParam(required = false) String comentario,
                               HttpSession session,
                               RedirectAttributes redirect) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || (!"SOPORTE".equals(usuario.getRol()) && !"ADMIN".equals(usuario.getRol()))) {
            redirect.addFlashAttribute("error", "No autorizado");
            return "redirect:/servicios";
        }
        servicioService.cambiarEstado(id, estado, comentario, usuario);
        redirect.addFlashAttribute("exito", "Estado actualizado");
        return "redirect:/servicios/" + id;
    }
    
    @PostMapping("/{id}/comentario")
    public String agregarComentario(@PathVariable Long id,
                                    @RequestParam String texto,
                                    HttpSession session,
                                    RedirectAttributes redirect) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || (!"SOPORTE".equals(usuario.getRol()) && !"ADMIN".equals(usuario.getRol()))) {
            redirect.addFlashAttribute("error", "No autorizado");
            return "redirect:/servicios";
        }
        servicioService.agregarComentario(id, texto, usuario);
        redirect.addFlashAttribute("exito", "Comentario agregado");
        return "redirect:/servicios/" + id;
    }
}