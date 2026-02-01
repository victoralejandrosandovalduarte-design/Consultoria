package com.example.Consultoria.TI.controller;

import org.springframework.ui.Model;
import com.example.Consultoria.TI.modelo.Usuario;
import com.example.Consultoria.TI.service.UsuarioService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * Sandoval
 */
@Controller
public class UsuarioController {
    @Autowired
    private UsuarioService service;
    @GetMapping("/formulario")
    public String registrarse(Model model) {
    model.addAttribute("usuario", new Usuario());
    return "usuarios/registrarse";
    }
    
    @PostMapping("/procesarRegistro")
    public String procesarRegistro(@ModelAttribute Usuario usuario){
    service.guardar(usuario);
    return "redirect:/login";
    }
    
    @GetMapping("/login")
    public String login (){
    return "usuarios/login";
    }
    @PostMapping("/procesarLogin")
    public String procesarLogin(@RequestParam String email, @RequestParam String clave,
                                                 HttpSession session, Model model, HttpServletResponse response){
                  response.setHeader("Cache-Control", "no-cache");
                  response.setHeader("Pragma", "no-cache");
                  response.setDateHeader("Expires", 0);
                  
                  Optional<Usuario> usuarioOpt = service.autenticar(email, clave);
                 if(usuarioOpt.isPresent()){
                 session.setAttribute("usuario", usuarioOpt.get());
                 //Redirigir vasado en rol. (puede ser admin a dashboard, soporte al servicio y cliente a solicitudes)
                 return "redirect:/principal";
                 }else{
                 model.addAttribute("error", "Credenciales inválidas");
                 return "usuarios/login";                 
                 }
    }
    @GetMapping("/principal")
    public String principal(HttpSession session, Model model){
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null) return "redirect:/login";
    // Logica por rol if ("SOPORTE".equals(usuario.getRol())) mostrar servicios pendientes
        model.addAttribute("usuarioConectado", "<b>Usuario: " + usuario.getEmail() + " (" + usuario.getRol() + ")</b>");
        return "principal";
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
    session.invalidate();
    return "redirect:/";
    }
}
