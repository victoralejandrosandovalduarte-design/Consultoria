package com.example.Consultoria.TI.modelo;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "material")
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMaterial;
    
    private String nombre;
    private Double precio;
}