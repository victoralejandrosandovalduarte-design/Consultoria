package com.example.Consultoria.TI.modelo;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "servicio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Servicio {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idServicio;
    private String moneda;
    private String numeroOrden;
    private LocalDateTime fechaHoraAgendamiento;
    private Integer tiempoEstimado;
    private Double presupuesto;
    private String estado;
    private String promocion;
    private String descuento;
    private String reclamo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCliente", nullable = false)
    private Cliente cliente;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idTecnico")
    private Tecnico tecnicoAsignado;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idTipoServicio", nullable = false)
    private TipoServicio tipoServicio;
    
    @OneToMany(mappedBy = "servicio", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DetalleServicio> detalles = new ArrayList<>();
    
    @OneToMany(mappedBy = "servicio", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comentario> comentarios = new ArrayList<>();
    
    // Métodos helper
    public void addComentario(Comentario comentario) {
        comentarios.add(comentario);
        comentario.setServicio(this);
    }
    
    public void addDetalle(DetalleServicio detalle) {
        detalles.add(detalle);
        detalle.setServicio(this);
    }
}