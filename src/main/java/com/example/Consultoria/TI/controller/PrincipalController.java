package com.example.Consultoria.TI.controller;

import com.example.Consultoria.TI.modelo.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PrincipalController {
    
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
        
        return "principal";
    }
}