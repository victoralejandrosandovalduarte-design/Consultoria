// ServicioService.java (updated with completions)
package com.example.Consultoria.TI.service;

import com.example.Consultoria.TI.modelo.*;
import com.example.Consultoria.TI.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicioService {
    
    private final ServicioRepository servicioRepository;
    private final ComentarioRepository comentarioRepository;
    private final ClienteRepository clienteRepository;
    private final TecnicoRepository tecnicoRepository;
    private final TipoServicioRepository tipoServicioRepository;
    private final MaterialRepository materialRepository;
    private final DetalleServicioRepository detalleServicioRepository;
    
    // Métodos CRUD básicos
    public List<Servicio> findAll() {
        return servicioRepository.findAll();
    }
    
    public Servicio findById(Long id) {
        return servicioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
    }
    
    @Transactional
public Servicio save(Servicio servicio) {
    generarNumeroOrden(servicio);
    return servicioRepository.save(servicio);
}
    
    @Transactional
    public void delete(Long id) {
        servicioRepository.deleteById(id);
    }
    
    // Métodos de consulta por estado
    public List<Servicio> findByEstado(String estado) {
        return servicioRepository.findByEstado(estado);
    }
    
    // Método para obtener servicios por cliente 
    public List<Servicio> obtenerServiciosPorCliente(Long idCliente) {
        return servicioRepository.findByClienteIdCliente(idCliente);
    }
    
    // Método para obtener servicios por técnico
    public List<Servicio> obtenerServiciosPorTecnico(Long idTecnico) {
        // Primero, verifica si el técnico existe
        Tecnico tecnico = tecnicoRepository.findById(idTecnico)
            .orElseThrow(() -> new RuntimeException("Técnico no encontrado"));
        
        // Luego, busca los servicios asignados a ese técnico
        return servicioRepository.findByTecnicoAsignadoIdTecnico(idTecnico);
    }
    
    // Métodos auxiliares para estadísticas
    public long countTotalServicios() {
        return servicioRepository.count();
    }
    
    public long countServiciosByEstado(String estado) {
        return servicioRepository.countByEstado(estado);
    }
    
    @Transactional
    public void asignarPresupuesto(Long id, Double presupuesto, String moneda, Tecnico tecnico) {
        Servicio servicio = findById(id);
        servicio.setPresupuesto(presupuesto);
        servicio.setMoneda(moneda);
        servicio.setTecnicoAsignado(tecnico);
        servicio.setEstado("ESPERANDO_APROBACION");
        save(servicio);
    }
    
    @Transactional
    public void aceptarServicio(Long id) {
        Servicio servicio = findById(id);
        if ("ESPERANDO_APROBACION".equals(servicio.getEstado())) {
            servicio.setEstado("EN_PROGRESO");
            save(servicio);
        }
    }
    
    @Transactional
    public void rechazarServicio(Long id, String comentario, Usuario usuario) {
        Servicio servicio = findById(id);
        if ("ESPERANDO_APROBACION".equals(servicio.getEstado())) {
            servicio.setEstado("CANCELADO");
            agregarComentario(id, comentario, usuario);
            save(servicio);
        }
    }
    @Transactional
    public void agregarDetalle(Long servicioId, Long materialId, Integer cantidad) {
        Servicio servicio = findById(servicioId);
        Material material = materialRepository.findById(materialId)
            .orElseThrow(() -> new RuntimeException("Material no encontrado"));
        
        DetalleServicio detalle = new DetalleServicio();
        detalle.setMaterial(material);
        detalle.setCantidad(cantidad);
        detalle.setSubtotal(cantidad * material.getPrecio());
        
        servicio.addDetalle(detalle);
        servicio.setPresupuesto(servicio.getPresupuesto() + detalle.getSubtotal()); // Actualizar presupuesto
        save(servicio);
    }
    
    @Transactional
    public void agregarComentario(Long servicioId, String texto, Usuario usuario) {
        Servicio servicio = findById(servicioId);
        
        Comentario comentario = Comentario.builder()
            .texto(texto)
            .usuario(usuario)
            .build();
        
        servicio.addComentario(comentario);
        save(servicio);
    }
    
    @Transactional
    public void cambiarEstado(Long id, String nuevoEstado, String comentario, Usuario usuario) {
        Servicio servicio = findById(id);
        servicio.setEstado(nuevoEstado);
        
        if (comentario != null && !comentario.isEmpty()) {
            agregarComentario(id, comentario, usuario);
        }
        
        save(servicio);
    }
    
    public List<Servicio> obtenerServiciosPorRol(Usuario usuario) {
        String rol = usuario.getRol();
        if ("ADMIN".equals(rol)) {
            return findAll();
        } else if ("SOPORTE".equals(rol)) {
            Tecnico tecnico = tecnicoRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Técnico no encontrado"));
            return obtenerServiciosPorTecnico(tecnico.getIdTecnico());
        } else if ("CLIENTE".equals(rol)) {
            return obtenerServiciosPorCliente(usuario.getCliente().getIdCliente());
        }
        return List.of();
    }
    
    // Método para generar número de orden automático
    @Transactional
    public Servicio generarNumeroOrden(Servicio servicio) {
        if (servicio.getNumeroOrden() == null) {
            long count = countTotalServicios() + 1;
            servicio.setNumeroOrden(String.format("ORD-%05d", count));
        }
        return servicio;
    }
}