package com.example.Consultoria.TI.controller;

import com.example.Consultoria.TI.modelo.Usuario;
import com.example.Consultoria.TI.service.ServicioService;
import com.example.Consultoria.TI.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PrincipalController {
    
    private final ServicioService servicioService;
    private final UsuarioService usuarioService;
    
    @GetMapping("/principal")
    public String principal(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/usuarios/login";
        }
        
        model.addAttribute("usuario", usuario);
        
        // Estadísticas básicas para todos los roles
        model.addAttribute("totalServicios", servicioService.countTotalServicios());
        model.addAttribute("serviciosPendientes", servicioService.countServiciosByEstado("PENDIENTE"));
        model.addAttribute("serviciosCompletados", servicioService.countServiciosByEstado("FINALIZADO"));
        
        // Estadísticas específicas por rol
        switch (usuario.getRol()) {
            case "ADMIN":
                // Para admin, cuenta todos los usuarios
                model.addAttribute("totalClientes", usuarioService.findAll().size());
                break;
                
            case "SOPORTE":
                // Para soporte, cuenta sus servicios asignados
                // Primero necesitas obtener el ID del técnico asociado a este usuario
                Long idTecnico = obtenerIdTecnicoPorUsuario(usuario);
                if (idTecnico != null) {
                    model.addAttribute("totalServicios", 
                        servicioService.countServiciosPorTecnico(idTecnico));
                }
                break;
                
            case "CLIENTE":
                // Para cliente, cuenta solo sus servicios
                if (usuario.getCliente() != null) {
                    model.addAttribute("totalServicios", 
                        servicioService.countServiciosPorCliente(usuario.getCliente().getIdCliente()));
                }
                break;
                
            default:
                model.addAttribute("totalClientes", 0);
                break;
        }
        
        return "principal";
    }
    
    // Método auxiliar para obtener el ID del técnico por usuario
    private Long obtenerIdTecnicoPorUsuario(Usuario usuario) {
        // Aquí implementa la lógica para obtener el técnico asociado al usuario
        // Por ahora, retornamos null (debes implementar según tu modelo)
        return null;
    }
}