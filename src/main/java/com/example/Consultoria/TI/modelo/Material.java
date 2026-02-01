
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

public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    
    private Long idMaterial;
    private String nombre;
    private Double precio;
    
    
}
