package com.example.Consultoria.TI.modelo;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cliente")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCliente;
    
    private String nombre;
    private String apellido;
    private String empresa;
    private String ciRuc;
    private String ciudad;
    private String lugarMantenimiento;
}