package com.example.Consultoria.TI.repository;

import com.example.Consultoria.TI.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmailAndClave(String email, String clave);
    Optional<Usuario> findByEmail(String email);
}