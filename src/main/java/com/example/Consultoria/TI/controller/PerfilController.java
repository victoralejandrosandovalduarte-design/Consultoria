package com.example.Consultoria.TI.controller;

import com.example.Consultoria.TI.modelo.Usuario;
import com.example.Consultoria.TI.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/perfil")
@RequiredArgsConstructor
public class PerfilController {
    
    private final UsuarioService usuarioService;
    
    @GetMapping
    public String verPerfil(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/usuarios/login";
        }
        model.addAttribute("usuario", usuario);
        return "perfil/ver"; // Nueva vista
    }
    
    @GetMapping("/editar")
    public String editarForm(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/usuarios/login";
        }
        model.addAttribute("usuario", usuario);
        return "perfil/formulario"; // Nueva vista
    }
    
    @PostMapping("/actualizar")
    public String actualizar(@RequestParam String email,
                             @RequestParam(required = false) String nuevaClave,
                             RedirectAttributes redirect,
                             HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/usuarios/login";
        }
        
        usuario.setEmail(email);
        if (nuevaClave != null && !nuevaClave.isEmpty()) {
            usuarioService.cambiarClave(usuario.getIdUsuario(), nuevaClave);
        }
        
        usuarioService.guardar(usuario);
        session.setAttribute("usuario", usuario); // Actualizar sesión
        redirect.addFlashAttribute("exito", "Perfil actualizado");
        return "redirect:/perfil";
    }
}