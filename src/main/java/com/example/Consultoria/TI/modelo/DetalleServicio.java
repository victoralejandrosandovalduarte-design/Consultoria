package com.example.Consultoria.TI.modelo;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "detalle_servicio")
public class DetalleServicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDetalle;
    
    private Integer cantidad;
    private Double subtotal;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idMaterial")
    private Material material;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idServicio")
    private Servicio servicio;
}