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
public class Tecnico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTecncio;
    private String nombre;
    
    @ManyToOne
    @JoinColumn(name = "idUsuario")
    private Usuario usuario; //vincula a Soporte
    
    
}
