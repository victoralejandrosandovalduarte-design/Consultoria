// ComentarioRepository.java
package com.example.Consultoria.TI.repository;
import com.example.Consultoria.TI.modelo.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {}