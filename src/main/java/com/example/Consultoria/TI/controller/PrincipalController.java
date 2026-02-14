package com.example.Consultoria.TI.controller;

import com.example.Consultoria.TI.modelo.Tecnico;
import com.example.Consultoria.TI.modelo.Usuario;
import com.example.Consultoria.TI.service.ClienteService;
import com.example.Consultoria.TI.service.ServicioService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PrincipalController {
    private final ServicioService servicioService;
    private final ClienteService clienteService;
    @GetMapping("/principal")
    public String principal(HttpSession session, Model model) {
        System.out.println("🔍 Accediendo a /principal");
        
        // Verificar sesión
        Object usuarioObj = session.getAttribute("usuario");
        System.out.println("   Usuario en sesión: " + usuarioObj);
        
        if (usuarioObj == null) {
            System.out.println("   ❌ No hay usuario, redirigiendo a login");
            return "redirect:/usuarios/login";
        }
        
        Usuario usuario = (Usuario) usuarioObj;
        System.out.println("   ✅ Usuario encontrado: " + usuario.getEmail() + " (" + usuario.getRol() + ")");
        
        // Agregar al modelo
        model.addAttribute("usuario", usuario);
        
       // Cargar estadísticas basadas en rol
        if ("ADMIN".equals(usuario.getRol())) {
            model.addAttribute("totalServicios", servicioService.countTotalServicios());
            model.addAttribute("serviciosPendientes", servicioService.countServiciosByEstado("PENDIENTE"));
            model.addAttribute("serviciosCompletados", servicioService.countServiciosByEstado("COMPLETADO"));
            model.addAttribute("totalClientes", clienteService.contarClientes());
        } else if ("SOPORTE".equals(usuario.getRol())) {
            // Estadísticas para soporte: servicios asignados
            Tecnico tecnico = (Tecnico) session.getAttribute("tecnico"); // Asumir que se guarda en sesión, o buscar
            model.addAttribute("totalServicios", servicioService.obtenerServiciosPorTecnico(tecnico.getIdTecnico()).size());
            model.addAttribute("serviciosPendientes", servicioService.obtenerServiciosPorTecnico(tecnico.getIdTecnico()).stream().filter(s -> "PENDIENTE".equals(s.getEstado())).count());
            model.addAttribute("serviciosCompletados", servicioService.obtenerServiciosPorTecnico(tecnico.getIdTecnico()).stream().filter(s -> "COMPLETADO".equals(s.getEstado())).count());
        } else if ("CLIENTE".equals(usuario.getRol())) {
            // Estadísticas para cliente: sus servicios
            model.addAttribute("totalServicios", servicioService.obtenerServiciosPorCliente(usuario.getCliente().getIdCliente()).size());
            model.addAttribute("serviciosPendientes", servicioService.obtenerServiciosPorCliente(usuario.getCliente().getIdCliente()).stream().filter(s -> "PENDIENTE".equals(s.getEstado())).count());
            model.addAttribute("serviciosCompletados", servicioService.obtenerServiciosPorCliente(usuario.getCliente().getIdCliente()).stream().filter(s -> "COMPLETADO".equals(s.getEstado())).count());
        }
        
        return "principal";
    }
}