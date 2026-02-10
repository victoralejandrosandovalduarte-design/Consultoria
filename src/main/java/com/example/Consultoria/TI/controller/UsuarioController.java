package com.example.Consultoria.TI.controller;

import com.example.Consultoria.TI.modelo.Usuario;
import com.example.Consultoria.TI.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    
    private final UsuarioService service;
    
    @GetMapping("/registro")
    public String registroForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuarios/registro";
    }
    
    @PostMapping("/procesarRegistro")
    public String procesarRegistro(@ModelAttribute Usuario usuario,
                                  RedirectAttributes redirect) {
        try {
            service.guardar(usuario);
            redirect.addFlashAttribute("exito", "Usuario registrado exitosamente");
            return "redirect:/usuarios/login";
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/usuarios/registro";
        }
    }
    
    @GetMapping("/login")
    public String loginForm(Model model) {
        return "usuarios/login";
    }
    
    @PostMapping("/procesarLogin")
    public String procesarLogin(@RequestParam String email, 
                               @RequestParam String clave,
                               HttpSession session, 
                               Model model,
                               RedirectAttributes redirect) {
        
        Optional<Usuario> usuarioOpt = service.autenticar(email, clave);
        if(usuarioOpt.isPresent()){
            Usuario usuario = usuarioOpt.get();
            session.setAttribute("usuario", usuario);
            redirect.addFlashAttribute("exito", "Bienvenido " + usuario.getEmail());
            return "redirect:/principal";
        } else {
            model.addAttribute("error", "Credenciales inválidas");
            return "usuarios/login";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirect) {
        session.invalidate();
        redirect.addFlashAttribute("exito", "Sesión cerrada exitosamente");
        return "redirect:/";
    }
}