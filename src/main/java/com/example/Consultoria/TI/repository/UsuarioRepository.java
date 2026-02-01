
package com.example.Consultoria.TI.repository;

import com.example.Consultoria.TI.modelo.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * Sandoval
 */
    public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
    Optional<Usuario> findByEmailAndClave(String email, String clave);        
    //

    }