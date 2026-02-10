package com.example.Consultoria.TI.controller;

import com.example.Consultoria.TI.modelo.Cliente;
import com.example.Consultoria.TI.service.ClienteService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {
    
    private final ClienteService clienteService;
    
    @GetMapping
    public String listarClientes(Model model, HttpSession session) {
        verificarSesion(session);
        model.addAttribute("clientes", clienteService.obtenerTodos());
        model.addAttribute("titulo", "Gestión de Clientes");
        return "clientes/lista";
    }
    
    @GetMapping("/nuevo")
    public String nuevoClienteForm(Model model, HttpSession session) {
        verificarSesion(session);
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("titulo", "Nuevo Cliente");
        return "clientes/formulario";
    }
    
    @PostMapping
    public String guardarCliente(@ModelAttribute Cliente cliente,
                                RedirectAttributes redirect,
                                HttpSession session) {
        verificarSesion(session);
        clienteService.guardar(cliente);
        redirect.addFlashAttribute("exito", "Cliente guardado exitosamente");
        return "redirect:/clientes";
    }
    
    @GetMapping("/{id}")
    public String verCliente(@PathVariable Long id, Model model, HttpSession session) {
        verificarSesion(session);
        model.addAttribute("cliente", clienteService.obtenerPorId(id));
        return "clientes/ver";
    }
    
    @GetMapping("/{id}/editar")
    public String editarClienteForm(@PathVariable Long id, 
                                   Model model, 
                                   HttpSession session) {
        verificarSesion(session);
        model.addAttribute("cliente", clienteService.obtenerPorId(id));
        return "clientes/formulario";
    }
    
    @PostMapping("/{id}/eliminar")
    public String eliminarCliente(@PathVariable Long id,
                                 RedirectAttributes redirect,
                                 HttpSession session) {
        verificarSesion(session);
        clienteService.eliminar(id);
        redirect.addFlashAttribute("exito", "Cliente eliminado");
        return "redirect:/clientes";
    }
    
    private void verificarSesion(HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            throw new RuntimeException("Acceso no autorizado");
        }
    }
}