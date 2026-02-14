// ServicioRepository.java
package com.example.Consultoria.TI.repository;

import com.example.Consultoria.TI.modelo.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {
    
    // Consultas derivadas del nombre
    List<Servicio> findByEstado(String estado);
    
    // Consulta por cliente (CORRECCIÓN)
    List<Servicio> findByClienteIdCliente(Long idCliente);
    
    // Consulta por técnico (CORRECCIÓN)
    List<Servicio> findByTecnicoAsignadoIdTecnico(Long idTecnico);
    
    // Consulta personalizada con @Query
    @Query("SELECT s FROM Servicio s WHERE s.estado IN :estados ORDER BY s.fechaHoraAgendamiento")
    List<Servicio> findByEstados(@Param("estados") List<String> estados);
    
    // Consulta con JOIN
    @Query("SELECT s FROM Servicio s JOIN s.cliente c WHERE c.empresa = :empresa")
    List<Servicio> findByEmpresaCliente(@Param("empresa") String empresa);
    
    // Métodos para conteo (NUEVOS)
    long countByEstado(String estado);
    
    @Query("SELECT COUNT(s) FROM Servicio s WHERE s.cliente.idCliente = :idCliente")
    long countByClienteIdCliente(@Param("idCliente") Long idCliente);
    
    @Query("SELECT COUNT(s) FROM Servicio s WHERE s.tecnicoAsignado.idTecnico = :idTecnico")
    long countByTecnicoAsignadoIdTecnico(@Param("idTecnico") Long idTecnico);
    
    // Consulta para servicios pendientes del día
    @Query("SELECT s FROM Servicio s WHERE s.estado = 'PENDIENTE' AND DATE(s.fechaHoraAgendamiento) = CURRENT_DATE")
    List<Servicio> findPendientesHoy();
    
    // Consulta para servicios del usuario logueado
    @Query("SELECT s FROM Servicio s WHERE " +
           "(:rol = 'ADMIN') OR " +
           "(:rol = 'SOPORTE' AND s.tecnicoAsignado.idTecnico = :idUsuario) OR " +
           "(:rol = 'CLIENTE' AND s.cliente.idCliente = :idCliente)")
    List<Servicio> findByRolUsuario(@Param("rol") String rol, 
                                    @Param("idUsuario") Long idUsuario,
                                    @Param("idCliente") Long idCliente);
}