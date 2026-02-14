package com.example.Consultoria.TI.repository;
import com.example.Consultoria.TI.modelo.Tecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TecnicoRepository extends JpaRepository<Tecnico, Long> {
    Optional<Tecnico> findByUsuarioIdUsuario(Long idUsuario);
}
