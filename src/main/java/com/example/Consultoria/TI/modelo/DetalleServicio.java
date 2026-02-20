package com.example.Consultoria.TI.modelo;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "detalle_servicio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DetalleServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long idDetalle;

    private Integer cantidad;
    private Double subtotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idMaterial")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Material material;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idServicio")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Servicio servicio;
}