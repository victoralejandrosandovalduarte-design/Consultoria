package com.example.Consultoria.TI.controller;

import com.example.Consultoria.TI.modelo.Usuario;
import com.example.Consultoria.TI.modelo.Cliente;
import com.example.Consultoria.TI.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    
    private final UsuarioService usuarioService;
    
    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        model.addAttribute("titulo", "Iniciar Sesión");
        return "usuarios/login";
    }
    
    @PostMapping("/login")
    public String procesarLogin(@RequestParam String email,
                               @RequestParam String clave,
                               HttpSession session,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        
        var usuarioOpt = usuarioService.autenticar(email, clave);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            session.setAttribute("usuario", usuario);
            
            // Redirección basada en rol
            switch (usuario.getRol()) {
                case "ADMIN":
                    return "redirect:/admin/dashboard";
                case "SOPORTE":
                    return "redirect:/soporte/dashboard";
                case "CLIENTE":
                    return "redirect:/cliente/dashboard";
                default:
                    return "redirect:/principal";
            }
        } else {
            model.addAttribute("error", "Credenciales incorrectas. Intente nuevamente.");
            return "usuarios/login";
        }
    }
    
    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("titulo", "Registro de Cliente");
        return "usuarios/registro";
    }
    
    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute Usuario usuario,
                                  RedirectAttributes redirectAttributes) {
        try {
            // Asignar rol de cliente por defecto
            usuario.setRol("CLIENTE");
            usuario.setEstado(true);
            
            // Si el usuario tiene cliente asociado, guardarlo también
            if (usuario.getCliente() != null) {
                // El cliente se guardará en cascada gracias a CascadeType.MERGE
            }
            
            usuarioService.save(usuario);
            redirectAttributes.addFlashAttribute("exito", 
                "¡Registro exitoso! Ahora puedes iniciar sesión.");
            return "redirect:/usuarios/login";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al registrar: " + e.getMessage());
            return "redirect:/usuarios/registro";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
    
    // Dashboard para cada rol
    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"ADMIN".equals(usuario.getRol())) {
            return "redirect:/usuarios/login";
        }
        model.addAttribute("usuario", usuario);
        return "admin/dashboard";
    }
    
    @GetMapping("/soporte/dashboard")
    public String soporteDashboard(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"SOPORTE".equals(usuario.getRol())) {
            return "redirect:/usuarios/login";
        }
        model.addAttribute("usuario", usuario);
        return "soporte/dashboard";
    }
    
    @GetMapping("/cliente/dashboard")
    public String clienteDashboard(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"CLIENTE".equals(usuario.getRol())) {
            return "redirect:/usuarios/login";
        }
        model.addAttribute("usuario", usuario);
        return "cliente/dashboard";
    }
    
    @GetMapping("/principal")
    public String principal(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/usuarios/login";
        }
        model.addAttribute("usuario", usuario);
        return "principal";
    }
}