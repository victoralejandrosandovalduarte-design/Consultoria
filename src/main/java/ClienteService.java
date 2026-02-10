// ClienteService.java - NUEVO
package com.example.Consultoria.TI.service;

import com.example.Consultoria.TI.modelo.Cliente;
import com.example.Consultoria.TI.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {
    
    private final ClienteRepository clienteRepository;
    
    public List<Cliente> obtenerTodos() {
        return clienteRepository.findAll();
    }
    
    public Cliente obtenerPorId(Long id) {
        return clienteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }
    
    @Transactional
    public Cliente guardar(Cliente cliente) {
        // Validaciones
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre es requerido");
        }
        if (cliente.getCiRuc() == null || cliente.getCiRuc().trim().isEmpty()) {
            throw new RuntimeException("El CI/RUC es requerido");
        }
        
        return clienteRepository.save(cliente);
    }
    
    @Transactional
    public void eliminar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new RuntimeException("Cliente no encontrado");
        }
        clienteRepository.deleteById(id);
    }
    
    public long contarClientes() {
        return clienteRepository.count();
    }
    
    public List<Cliente> buscarPorCiudad(String ciudad) {
        // Implementar consulta personalizada en ClienteRepository
        return clienteRepository.findAll().stream()
            .filter(c -> ciudad.equalsIgnoreCase(c.getCiudad()))
            .toList();
    }
}