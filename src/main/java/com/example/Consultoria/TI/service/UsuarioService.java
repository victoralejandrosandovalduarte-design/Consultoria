package com.example.Consultoria.TI.service;

import com.example.Consultoria.TI.modelo.Usuario;
import com.example.Consultoria.TI.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    
    public Optional<Usuario> autenticar(String email, String clave) {
        return usuarioRepository.findByEmailAndClave(email, clave);
    }
    
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }
    
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }
    
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }
    
    public void delete(Long id) {
        usuarioRepository.deleteById(id);
    }
}