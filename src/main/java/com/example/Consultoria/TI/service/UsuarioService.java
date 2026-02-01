package com.example.Consultoria.TI.service;

import com.example.Consultoria.TI.modelo.Usuario;
import com.example.Consultoria.TI.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * Sandoval
 */
@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository repository;
    
    public Optional<Usuario> autenticar(String email, String clave){
        return repository.findByEmailAndClave(email,clave);
    }
    public void guardar (Usuario usuario){
    repository.save(usuario);
    }
    public List<Usuario> obetenerTodos(){
    return repository.findAll();
    }
    //Revisar!! Logica p/roles(Soporte=habilitado para cambiar estado)
}
