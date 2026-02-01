package com.example.Consultoria.TI.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

/**
 *
 * Sandoval
 */


@Data
@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    
    private Long idUsuario;
    private String email;
    private String clave;
    private String rol; //  "Admin", "Soporte", "Cliente"
    private Boolean estado; //Activo/Inactivo
    
    
    @OneToOne
    @JoinColumn(name= "idCliente", unique = true)
    private Cliente cliente;
    
    }