package com.example.Consultoria.TI.modelo;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tecnico")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tecnico {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tecnico") // ¡AGREGA ESTO!
    private Long idTecnico;
    
    private String nombre;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
}