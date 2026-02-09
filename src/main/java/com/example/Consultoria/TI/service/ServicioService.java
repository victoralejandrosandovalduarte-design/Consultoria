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
    
    public long countServiciosPorCliente(Long idCliente) {
        return servicioRepository.countByClienteIdCliente(idCliente);
    }
    
    public long countServiciosPorTecnico(Long idTecnico) {
        return servicioRepository.countByTecnicoAsignadoIdTecnico(idTecnico);
    }
    
    // Método para cambiar estado
    @Transactional
    public Servicio cambiarEstado(Long id, String nuevoEstado, String comentarioTexto, Usuario usuario) {
        Servicio servicio = findById(id);
        servicio.setEstado(nuevoEstado);
        
        if (comentarioTexto != null && !comentarioTexto.trim().isEmpty()) {
            Comentario comentario = Comentario.builder()
                .texto("Estado cambiado a " + nuevoEstado + ": " + comentarioTexto)
                .fecha(LocalDateTime.now())
                .servicio(servicio)
                .usuario(usuario)
                .build();
            
            comentarioRepository.save(comentario);
            servicio.addComentario(comentario);
        }
        
        return servicioRepository.save(servicio);
    }
    
    // Métodos para dropdowns
    public List<Cliente> obtenerTodosClientes() {
        return clienteRepository.findAll();
    }
    
    public List<Tecnico> obtenerTodosTecnicos() {
        return tecnicoRepository.findAll();
    }
    
    public List<TipoServicio> obtenerTodosTiposServicio() {
        return tipoServicioRepository.findAll();
    }
    
    public List<Material> obtenerTodosMateriales() {
        return materialRepository.findAll();
    }
}