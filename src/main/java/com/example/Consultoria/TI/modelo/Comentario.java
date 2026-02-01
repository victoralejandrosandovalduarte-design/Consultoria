
package com.example.Consultoria.TI.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Date;
import lombok.Data;

/**
 *
 * Sandoval
 */
@Data
@Entity

public class Comentario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    
    private Long idComentario;
    private String texto;
    private Date fecha;
    
    @ManyToOne
    @JoinColumn(name = "idServicio")
    private Servicio servicio;
    
    @ManyToOne
    @JoinColumn(name = "idUsuario")
    private Usuario usuario; // Quien comenta (Soporte/Cliente)   
   
    
}
