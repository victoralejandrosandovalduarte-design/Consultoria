// DetalleServicioRepository.java
package com.example.Consultoria.TI.repository;

import com.example.Consultoria.TI.modelo.DetalleServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleServicioRepository extends JpaRepository<DetalleServicio, Long> {}