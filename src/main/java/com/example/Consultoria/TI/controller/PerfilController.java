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
    Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
    if (usuarioSesion == null) return "redirect:/usuarios/login";

    try {
        // Actualizar email
        usuarioService.actualizarEmail(usuarioSesion.getIdUsuario(), email);

        // Actualizar clave si se proporcionó
        if (nuevaClave != null && !nuevaClave.isEmpty()) {
            usuarioService.cambiarClave(usuarioSesion.getIdUsuario(), nuevaClave);
        }

        // Recargar usuario actualizado en sesión
        Usuario usuarioActualizado = usuarioService.findById(usuarioSesion.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        session.setAttribute("usuario", usuarioActualizado);

        redirect.addFlashAttribute("exito", "Perfil actualizado");
    } catch (Exception e) {
        redirect.addFlashAttribute("error", "Error al actualizar: " + e.getMessage());
    }
    return "redirect:/perfil";
}
}