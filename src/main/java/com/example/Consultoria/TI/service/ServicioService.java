package com.example.Consultoria.TI.service;

import com.example.Consultoria.TI.modelo.Servicio;
import com.example.Consultoria.TI.repository.ServicioRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * Sandoval
 */
@Service
public class ServicioService {
    @Autowired
    private ServicioRepository repository; // inyecta el repositor
    
    @Transactional // Se usa esta anotación para inserciòn atómica en cabecera-detalle
    public void guardarServicio(Servicio servicio){
        //Logica; asignar técnico, calcular presupuesto...
        repository.save(servicio);
    }
    
    public void cambiarEstado(Long id, String nuevoEstado, String comentario){
    Servicio servicio = repository.findById(id).orElseThrow();
    // Agregar comentario si no finalizada
    if (!"FINALIZADA".equals(nuevoEstado)){
    //Crar y agregar comentario
    }
    repository.save(servicio);
    // Métodos para promociones, descuentos, reclamos, impresión
    }
    public Servicio save(Servicio servicio) {
        return repository.save(servicio);  // ahora compila
    }

    public Optional<Servicio> findById(Long id) {
        return repository.findById(id);  // ahora compila
    }

    public Servicio update(Servicio servicio) {
        return repository.save(servicio);  // save también sirve para update
    }

    // 
    //REVISAAR!!!!!
    }
