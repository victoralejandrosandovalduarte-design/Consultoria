package com.example.Consultoria.TI.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

/**
 *
 * Sandoval
 */

    @Data
    @Entity
    
public class TipoServicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTipoServicio;
    private String nombre; //"consultoría, Redes, Hardware"
    private Boolean certificada; // Para capacitaciones
    
}
