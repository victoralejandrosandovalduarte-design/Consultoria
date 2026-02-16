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
public String listar(@RequestParam(required = false) String estado, Model model, HttpSession session) {
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null) return "redirect:/usuarios/login";
    
    List<Servicio> servicios;
    if ("ADMIN".equals(usuario.getRol())) {
        servicios = servicioService.findAll();
    } else if ("SOPORTE".equals(usuario.getRol())) {
        Tecnico tecnico = (Tecnico) session.getAttribute("tecnico");
        if (tecnico == null) {
            model.addAttribute("error", "Técnico no asignado. Contacta al admin.");
            return "servicios/lista";
        }
        servicios = servicioService.obtenerServiciosPorTecnico(tecnico.getIdTecnico());
        System.out.println("DEBUG Soporte: Servicios cargados: " + servicios.size()); // Log para verificar
    } else {
        servicios = servicioService.obtenerServiciosPorCliente(usuario.getCliente().getIdCliente());
    }
    if (estado != null) {
        servicios = servicios.stream().filter(s -> estado.equals(s.getEstado())).toList();
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

    try {
        // Validaciones básicas
        if (servicio.getCliente() == null || servicio.getCliente().getIdCliente() == null) {
            throw new RuntimeException("Debe seleccionar un cliente");
        }
        if (servicio.getTipoServicio() == null || servicio.getTipoServicio().getIdTipoServicio() == null) {
            throw new RuntimeException("Debe seleccionar un tipo de servicio");
        }
        servicioService.save(servicio);
        redirect.addFlashAttribute("exito", "Servicio guardado exitosamente");
    } catch (Exception e) {
        redirect.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
        return "redirect:/servicios/nuevo";
    }
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
        model.addAttribute("mostrarPresupuesto", !"SOPORTE".equals(usuario.getRol()));
        return "servicios/ver";
    }
    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Long id, Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"ADMIN".equals(usuario.getRol())) {
            return "redirect:/servicios";
        }
        
        Servicio servicio = servicioService.findById(id);
        model.addAttribute("servicio", servicio);
        model.addAttribute("tecnicos", tecnicoRepository.findAll());
        model.addAttribute("tipos", tipoServicioRepository.findAll());
        return "servicios/formulario";
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
    return "redirect:/servicios/" + id; // Evita Whitelabel
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
    @PostMapping("/{id}/asignar")
    public String asignarPresupuesto(@PathVariable Long id,
                                     @RequestParam Double presupuesto,
                                     @RequestParam String moneda,
                                     @RequestParam Long tecnicoId,
                                     RedirectAttributes redirect,
                                     HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"ADMIN".equals(usuario.getRol())) {
            redirect.addFlashAttribute("error", "No autorizado");
            return "redirect:/servicios";
            
        }
        Tecnico tecnico = tecnicoRepository.findById(tecnicoId)
            .orElseThrow(() -> new RuntimeException("Técnico no encontrado"));
        
        servicioService.asignarPresupuesto(id, presupuesto, moneda, tecnico);
        redirect.addFlashAttribute("exito", "Presupuesto asignado");
        return "redirect:/servicios/" + id;
    }
    @PostMapping("/{id}/aceptar")
    public String aceptar(@PathVariable Long id, RedirectAttributes redirect, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"CLIENTE".equals(usuario.getRol())) {
            redirect.addFlashAttribute("error", "No autorizado");
            return "redirect:/servicios";
        }
        
        Servicio servicio = servicioService.findById(id);
        if (!servicio.getCliente().getIdCliente().equals(usuario.getCliente().getIdCliente())) {
            redirect.addFlashAttribute("error", "No autorizado");
            return "redirect:/servicios";
        }
        
        servicioService.aceptarServicio(id);
        redirect.addFlashAttribute("exito", "Servicio aceptado");
        return "redirect:/servicios/" + id;
    }
    @PostMapping("/{id}/rechazar")
    public String rechazar(@PathVariable Long id,
                           @RequestParam String comentario,
                           RedirectAttributes redirect,
                           HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"CLIENTE".equals(usuario.getRol())) {
            redirect.addFlashAttribute("error", "No autorizado");
            return "redirect:/servicios";
        }
        
        Servicio servicio = servicioService.findById(id);
        if (!servicio.getCliente().getIdCliente().equals(usuario.getCliente().getIdCliente())) {
            redirect.addFlashAttribute("error", "No autorizado");
            return "redirect:/servicios";
        }
        servicioService.rechazarServicio(id, comentario, usuario);
        redirect.addFlashAttribute("exito", "Servicio rechazado");
        return "redirect:/servicios/" + id;
    }
    }