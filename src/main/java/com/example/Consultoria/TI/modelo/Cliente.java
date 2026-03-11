package com.example.Consultoria.TI.modelo;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cliente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long idCliente;

    private String nombre;
    private String apellido;
    private String empresa;
    private String ciRuc;
    private String ciudad;
    private String lugarMantenimiento;
    private String pais;
    private String departamento;
    private String barrio;
}