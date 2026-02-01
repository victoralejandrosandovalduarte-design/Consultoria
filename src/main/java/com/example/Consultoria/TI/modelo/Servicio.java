package com.example.Consultoria.TI.modelo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.Date;
import lombok.Data;
import java.util.List;

/**
 *
 * Sandoval
 */
    @Data
    @Entity

public class Servicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    
    private Long idServicio;
    private String numeroOrden; // Solped
    private Date fechaHoraAgendamiento;
    private Integer tiempoEstimado; // En horas
    private Double prespuesto; // OC
    private String estado; // Pendiente, en progreso, finalizado
    private String promocion;
    private String descuento;
    private String reclamo; // Con seguimiento
   
    @ManyToOne
    @JoinColumn(name = "idCliente")
    private Cliente cliente;
    
    @ManyToOne
    @JoinColumn(name = "idTecnico")
    private Tecnico tecnicoAsignado;
    
    @ManyToOne
    @JoinColumn(name= "idTipoServicio")
    private TipoServicio tiposervicio;
    
    @OneToMany(mappedBy = "servicio", cascade = CascadeType.ALL)
    private List<DetalleServicio> detalles; //Para Ticket-like
    
    @OneToMany(mappedBy = "servicio", cascade = CascadeType.ALL)
    private List<Comentario> comentarios; // Para Ticket-like
    
}
