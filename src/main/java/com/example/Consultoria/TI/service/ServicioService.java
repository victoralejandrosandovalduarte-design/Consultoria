package com.example.Consultoria.TI.service;

import com.example.Consultoria.TI.modelo.*;
import com.example.Consultoria.TI.repository.*;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime; // voy a usar luego XD
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
    private LocalDateTime fechaCreacion;
    
    @Transactional(readOnly = true)
public Servicio findByIdWithDetails(Long id) {
    return servicioRepository.findByIdWithDetails(id)
        .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
}
    // Métodos CRUD básicos
     @Transactional(readOnly = true)
    public List<Servicio> findAll() {
        return servicioRepository.findAllWithDetails(); // cambia a la versión con fetch
    }
    
    public Servicio findById(Long id) {
        return servicioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
    }
    
    @Transactional
public Servicio save(Servicio servicio) {
    generarNumeroOrden(servicio);
    if (servicio.getFechaCreacion() == null) {
    servicio.setFechaCreacion(LocalDateTime.now());
}
    return servicioRepository.save(servicio);
}
@Transactional
public void cancelarServicio(Long id, String comentario, Usuario usuario) {
    Servicio servicio = findById(id);
    servicio.setEstado("CANCELADO");
    agregarComentario(id, comentario, usuario);
    save(servicio);
}
@Transactional
public void eliminar(Long id) {
    // Verificar que no haya restricciones (opcional)
    servicioRepository.deleteById(id);
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
   @Transactional(readOnly = true)
    public List<Servicio> obtenerServiciosPorCliente(Long idCliente) {
        return servicioRepository.findByClienteIdClienteWithDetails(idCliente);
    }
    
    // Método para obtener servicios por técnico
     @Transactional(readOnly = true)
    public List<Servicio> obtenerServiciosPorTecnico(Long idTecnico) {
        return servicioRepository.findByTecnicoAsignadoIdTecnicoWithDetails(idTecnico);
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
        servicio.setEstado("PENDIENTE_ASIGNACION_TECNICO");
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
    recalcularPresupuesto(servicio);
    servicio.addDetalle(detalle);
    // Actualizar presupuesto
    if (servicio.getPresupuesto() == null) {
        servicio.setPresupuesto(0.0);
    }
    servicio.setPresupuesto(servicio.getPresupuesto() + detalle.getSubtotal());
    save(servicio);
}
@Transactional
public void eliminarDetalle(Long detalleId) {
    DetalleServicio detalle = detalleServicioRepository.findById(detalleId)
        .orElseThrow(() -> new RuntimeException("Detalle no encontrado"));
    Servicio servicio = detalle.getServicio();
    servicio.setPresupuesto(servicio.getPresupuesto() - detalle.getSubtotal());
    servicio.getDetalles().remove(detalle);
    detalleServicioRepository.delete(detalle);
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
public void generarPresupuesto(Long id) {
    Servicio servicio = findById(id);
    if (!"PENDIENTE".equals(servicio.getEstado())) {
        throw new RuntimeException("El servicio no está en estado pendiente");
    }
    servicio.setEstado("ESPERANDO_APROBACION");
    save(servicio);
}
@Transactional
public void actualizarManoObra(Long id, Double costoManoObra) {
    Servicio servicio = findById(id);
    servicio.setCostoManoObra(costoManoObra);
    recalcularPresupuesto(servicio);
    save(servicio);
}

@Transactional
public void asignarTecnico(Long id, Tecnico tecnico) {
    Servicio servicio = findById(id);
    if (!"PENDIENTE_ASIGNACION_TECNICO".equals(servicio.getEstado())) {
        throw new RuntimeException("El servicio no está esperando asignación de técnico");
    }
    servicio.setTecnicoAsignado(tecnico);
    servicio.setEstado("EN_PROGRESO");
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
    private void recalcularPresupuesto(Servicio servicio) {
    double totalMateriales = servicio.getDetalles().stream()
            .mapToDouble(DetalleServicio::getSubtotal).sum();
    double costoManoObra = servicio.getCostoManoObra() != null ? servicio.getCostoManoObra() : 0.0;
    servicio.setPresupuesto(totalMateriales + costoManoObra);
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