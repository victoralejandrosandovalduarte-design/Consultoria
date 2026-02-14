// TipoServicioRepository.java
package com.example.Consultoria.TI.repository;
import com.example.Consultoria.TI.modelo.TipoServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoServicioRepository extends JpaRepository<TipoServicio, Long> {}