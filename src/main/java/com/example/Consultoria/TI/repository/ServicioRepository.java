package com.example.Consultoria.TI.repository;

/**
 *
 * Sandoval
 */

import com.example.Consultoria.TI.modelo.Servicio;  // import de tu modelo
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicioRepository extends JpaRepository<Servicio, Long> {
    // se puede agregar métodos extras pero save y findById ya están ok
}