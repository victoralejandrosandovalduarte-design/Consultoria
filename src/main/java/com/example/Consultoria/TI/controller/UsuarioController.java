package com.example.Consultoria.TI.controller;

import com.example.Consultoria.TI.modelo.Cliente;
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
        model.addAttribute("cliente", new Cliente()); // Agregamos cliente para el form
        return "usuarios/registro";
    }
    
  @PostMapping("/procesarRegistro")
public String procesarRegistro(@RequestParam String email,
                               @RequestParam String clave,
                               @RequestParam String nombre,
                               @RequestParam String apellido,
                               @RequestParam String empresa,
                               @RequestParam String ciRuc,
                               @RequestParam(required = false) String ciudad,
                               @RequestParam(required = false) String lugarMantenimiento,
                               RedirectAttributes redirect) {
    try {
        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setClave(clave); // se encriptará en el servicio
        usuario.setRol("CLIENTE");
        usuario.setEstado(true);

        // Crear cliente
        Cliente cliente = Cliente.builder()
                .nombre(nombre)
                .apellido(apellido)
                .empresa(empresa)
                .ciRuc(ciRuc)
                .ciudad(ciudad)
                .lugarMantenimiento(lugarMantenimiento)
                .build();
        usuario.setCliente(cliente);

        // Guardar (el servicio encripta la clave y guarda en cascada)
        service.guardar(usuario);

        redirect.addFlashAttribute("exito", "Registro exitoso. Por favor inicia sesión.");
        return "redirect:/usuarios/login";
    } catch (Exception e) {
        e.printStackTrace();
        redirect.addFlashAttribute("error", "Error en el registro: " + e.getMessage());
        return "redirect:/usuarios/registro";
    }
}
    
    @GetMapping("/login")
    public String loginForm(Model model) {
        return "usuarios/login";
    }
    
    @PostMapping("/procesarLogin")
    public String procesarLogin(
                               @RequestParam String email, 
                               @RequestParam String clave,
                               HttpSession session, 
                               Model model,
                               RedirectAttributes redirect) {
        try {
            // Buscar todos los usuarios y encontrar por email manualmente
            // para evitar cualquier problema con el query derivado
            Usuario usuario = service.findAll().stream()
                .filter(u -> email != null && email.equalsIgnoreCase(u.getEmail()))
                .findFirst()
                .orElse(null);

            if (usuario == null) {
                model.addAttribute("error", "Credenciales inválidas");
                return "usuarios/login";
            }

            if (!Boolean.TRUE.equals(usuario.getEstado())) {
                model.addAttribute("error", "Cuenta desactivada");
                return "usuarios/login";
            }

            if (!service.verificarClave(clave, usuario.getClave())) {
                model.addAttribute("error", "Credenciales inválidas");
                return "usuarios/login";
            }

            session.setAttribute("usuario", usuario);
            redirect.addFlashAttribute("exito", "Bienvenido " + usuario.getEmail());
            return "redirect:/principal";

        } catch (Exception e) {
            model.addAttribute("error", "Error interno: " + e.getMessage());
            return "usuarios/login";
        }
    }
    @GetMapping
    public String listarUsuarios(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"ADMIN".equals(usuario.getRol())) {
            return "redirect:/principal";
        }
        model.addAttribute("usuarios", service.findAll());
        return "usuarios/lista"; // Crear esta vista
    }
    
    @GetMapping("/nuevo")
    public String nuevoUsuarioForm(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"ADMIN".equals(usuario.getRol())) {
            return "redirect:/principal";
        }
        model.addAttribute("usuario", new Usuario());
        return "usuarios/formulario"; // Crear esta vista, similar a registro pero con todos los roles
    }
    
    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario, RedirectAttributes redirect, HttpSession session) {
        Usuario admin = (Usuario) session.getAttribute("usuario");
        if (admin == null || !"ADMIN".equals(admin.getRol())) {
            return "redirect:/principal";
        }
        service.guardar(usuario);
        redirect.addFlashAttribute("exito", "Usuario guardado");
        return "redirect:/usuarios";
    }
    
    @GetMapping("/{id}/editar")
    public String editarUsuario(@PathVariable Long id, Model model, HttpSession session) {
        Usuario admin = (Usuario) session.getAttribute("usuario");
        if (admin == null || !"ADMIN".equals(admin.getRol())) {
            return "redirect:/principal";
        }
        model.addAttribute("usuario", service.findById(id).orElseThrow());
        return "usuarios/formulario";
    }
    
    @PostMapping("/{id}/eliminar")
public String eliminarUsuario(@PathVariable Long id, RedirectAttributes redirect, HttpSession session) {
    Usuario admin = (Usuario) session.getAttribute("usuario");
    if (admin == null || !"ADMIN".equals(admin.getRol())) {
        return "redirect:/principal";
    }
    try {
        service.eliminar(id);
        redirect.addFlashAttribute("exito", "Usuario eliminado correctamente.");
    } catch (RuntimeException e) {
        redirect.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/usuarios";
}    
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirect) {
        session.invalidate();
        redirect.addFlashAttribute("exito", "Sesión cerrada exitosamente");
        return "redirect:/";
    }
}