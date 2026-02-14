// UsuarioRepository.java
package com.example.Consultoria.TI.repository;
import com.example.Consultoria.TI.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Este método TIENE existir para el login
    Optional<Usuario> findByEmailAndClave(String email, String clave);
    
    // Este método es útil para validar email único
    Optional<Usuario> findByEmail(String email);
}