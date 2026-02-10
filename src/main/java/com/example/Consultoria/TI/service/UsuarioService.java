// UsuarioService.java - MEJORADO
package com.example.Consultoria.TI.service;

import com.example.Consultoria.TI.modelo.Usuario;
import com.example.Consultoria.TI.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    
    private final UsuarioRepository repository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Transactional
    public void guardar(Usuario usuario) {
        // Validar email único
        if (repository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }
        
        // Encriptar contraseña
        usuario.setClave(passwordEncoder.encode(usuario.getClave()));
        
        // Valores por defecto
        if (usuario.getRol() == null) {
            usuario.setRol("CLIENTE");
        }
        if (usuario.getEstado() == null) {
            usuario.setEstado(true);
        }
        
        repository.save(usuario);
    }
    
    public Optional<Usuario> autenticar(String email, String clave) {
        Optional<Usuario> usuarioOpt = repository.findByEmail(email);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (passwordEncoder.matches(clave, usuario.getClave()) 
                && Boolean.TRUE.equals(usuario.getEstado())) {
                return Optional.of(usuario);
            }
        }
        return Optional.empty();
    }
    
    public List<Usuario> obtenerTodos() {
        return repository.findAll();
    }
    
    @Transactional
    public void cambiarEstado(Long id, Boolean estado) {
        repository.findById(id).ifPresent(usuario -> {
            usuario.setEstado(estado);
            repository.save(usuario);
        });
    }
    
    @Transactional
    public void cambiarRol(Long id, String rol) {
        repository.findById(id).ifPresent(usuario -> {
            usuario.setRol(rol);
            repository.save(usuario);
        });
    }
}