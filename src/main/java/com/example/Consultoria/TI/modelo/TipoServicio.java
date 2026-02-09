package com.example.Consultoria.TI.modelo;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipo_servicio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder  // ¡FALTABA ESTA ANOTACIÓN!
public class TipoServicio {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTipoServicio;
    
    private String nombre;
    private Boolean certificada;
}