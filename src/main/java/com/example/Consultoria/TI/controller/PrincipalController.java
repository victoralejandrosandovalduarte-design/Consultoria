package com.example.Consultoria.TI.controller;

import com.example.Consultoria.TI.modelo.Servicio;
import com.example.Consultoria.TI.modelo.Tecnico;
import com.example.Consultoria.TI.modelo.Usuario;
import com.example.Consultoria.TI.repository.TecnicoRepository;
import com.example.Consultoria.TI.service.ClienteService;
import com.example.Consultoria.TI.service.ServicioService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PrincipalController {
    private final ServicioService servicioService;
    private final ClienteService clienteService;
    private final TecnicoRepository tecnicoRepository;
    
    
   @GetMapping("/principal")
    public String principal(HttpSession session, Model model) {
        Object usuarioObj = session.getAttribute("usuario");
        if (usuarioObj == null) {
            return "redirect:/usuarios/login";
        }
        
        Usuario usuario = (Usuario) usuarioObj;
        model.addAttribute("usuario", usuario);
        
       // Cargar estadísticas
        long totalServicios = 0;
        long serviciosPendientes = 0;
        long serviciosCompletados = 0;
        long totalClientes = 0;
        
        if ("ADMIN".equals(usuario.getRol())) {
            totalServicios = servicioService.countTotalServicios();
            serviciosPendientes = servicioService.countServiciosByEstado("PENDIENTE");
            serviciosCompletados = servicioService.countServiciosByEstado("COMPLETADO");
            totalClientes = clienteService.contarClientes();
        } else if ("SOPORTE".equals(usuario.getRol())) {
            Optional<Tecnico> tecnicoOpt = tecnicoRepository.findByUsuarioIdUsuario(usuario.getIdUsuario());
            if (tecnicoOpt.isPresent()) {
                Tecnico tecnico = tecnicoOpt.get();
                session.setAttribute("tecnico", tecnico); // Guardar en sesión
                List<Servicio> servicios = servicioService.obtenerServiciosPorTecnico(tecnico.getIdTecnico());
                totalServicios = servicios.size();
                serviciosPendientes = servicios.stream().filter(s -> "PENDIENTE".equals(s.getEstado())).count();
                serviciosCompletados = servicios.stream().filter(s -> "COMPLETADO".equals(s.getEstado())).count();
            }
        } else if ("CLIENTE".equals(usuario.getRol())) {
            List<Servicio> servicios = servicioService.obtenerServiciosPorCliente(usuario.getCliente().getIdCliente());
            totalServicios = servicios.size();
            serviciosPendientes = servicios.stream().filter(s -> "PENDIENTE".equals(s.getEstado())).count();
            serviciosCompletados = servicios.stream().filter(s -> "COMPLETADO".equals(s.getEstado())).count();
        }
        model.addAttribute("totalServicios", totalServicios);
        model.addAttribute("serviciosPendientes", serviciosPendientes);
        model.addAttribute("serviciosCompletados", serviciosCompletados);
        model.addAttribute("totalClientes", totalClientes);
        return "principal";
    }
}