package com.example.Consultoria.TI.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

/**
 *
 * Sandoval
 */
@Data
@Entity
public class DetalleServicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    
    private Long idDetalle;
    private Integer cantidad;
    private Double subtotal;
    
    @ManyToOne
    @JoinColumn(name= "idMaterial")
    private Material material;
    @ManyToOne
    @JoinColumn(name= "idServicio")
    private Servicio servicio;
    }
