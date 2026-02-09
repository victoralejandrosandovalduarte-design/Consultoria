package com.example.Consultoria.TI.modelo;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    private String clave;
    private String rol;
    private Boolean estado;
    
    // CAMBIA CascadeType.ALL por CascadeType.MERGE
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;
}