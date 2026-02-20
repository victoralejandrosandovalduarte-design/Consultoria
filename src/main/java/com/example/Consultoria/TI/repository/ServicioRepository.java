// ServicioRepository.java
package com.example.Consultoria.TI.repository;

import com.example.Consultoria.TI.modelo.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;




@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {

    List<Servicio> findByEstado(String estado);

    // Para cliente: carga cliente, tipoServicio y comentarios con usuario
    @Query("SELECT DISTINCT s FROM Servicio s " +
           "LEFT JOIN FETCH s.cliente c " +
           "LEFT JOIN FETCH s.tipoServicio ts " +
            "LEFT JOIN FETCH s.tecnicoAsignado ta " +
           "LEFT JOIN FETCH s.comentarios com " +
           "LEFT JOIN FETCH com.usuario u " +
           "WHERE s.cliente.idCliente = :idCliente " +
           "ORDER BY s.fechaHoraAgendamiento DESC")
    List<Servicio> findByClienteIdClienteWithDetails(@Param("idCliente") Long idCliente);
    // Para técnico (soporte)
    @Query("SELECT DISTINCT s FROM Servicio s " +
           "LEFT JOIN FETCH s.cliente c " +
           "LEFT JOIN FETCH s.tipoServicio ts " +
            "LEFT JOIN FETCH s.tecnicoAsignado ta " +
           "LEFT JOIN FETCH s.comentarios com " +
           "LEFT JOIN FETCH com.usuario u " +
           "WHERE s.tecnicoAsignado.idTecnico = :idTecnico " +
           "ORDER BY s.fechaHoraAgendamiento DESC")
             List<Servicio> findByTecnicoAsignadoIdTecnicoWithDetails(@Param("idTecnico") Long idTecnico);
              // Para admin: todos los servicios con detalles
    @Query("SELECT DISTINCT s FROM Servicio s " +
           "LEFT JOIN FETCH s.cliente c " +
           "LEFT JOIN FETCH s.tipoServicio ts " +
            "LEFT JOIN FETCH s.tecnicoAsignado ta " +
           "LEFT JOIN FETCH s.comentarios com " +
           "LEFT JOIN FETCH com.usuario u " +
           "ORDER BY s.fechaHoraAgendamiento DESC")
    // Consulta por cliente (CORRECCIÓN)
            List<Servicio> findAllWithDetails();
            long countByEstado(String estado);
@Query("SELECT COUNT(s) FROM Servicio s WHERE s.cliente.idCliente = :idCliente")
long countByClienteIdCliente(@Param("idCliente") Long idCliente);
    // Métodos de conteo (se mantienen)   2.-cargar un servicio con todas sus relaciones:    
@Query("SELECT DISTINCT s FROM Servicio s " +
       "LEFT JOIN FETCH s.cliente " +
       "LEFT JOIN FETCH s.tipoServicio " +
       "LEFT JOIN FETCH s.comentarios com " +
       "LEFT JOIN FETCH com.usuario " +
       "LEFT JOIN FETCH s.detalles det " +
       "LEFT JOIN FETCH det.material " +
       "LEFT JOIN FETCH s.tecnicoAsignado " +
       "WHERE s.idServicio = :id")
Optional<Servicio> findByIdWithDetails(@Param("id") Long id);
}