// Comentario.java
package com.example.Consultoria.TI.modelo;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comentario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comentario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idComentario;
    
    @Lob
    private String texto;
    
    @Builder.Default
    private LocalDateTime fecha = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idServicio")
    private Servicio servicio;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idUsuario")
    private Usuario usuario;
}