package com.example.Consultoria.TI.modelo;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

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
    private LocalDateTime fechaCreacion;  // Añadido para la fecha de creación
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

    // Cambiado de List a Set para evitar MultipleBagFetchException
    @OneToMany(mappedBy = "servicio", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<DetalleServicio> detalles = new LinkedHashSet<>();

    @OneToMany(mappedBy = "servicio", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Comentario> comentarios = new LinkedHashSet<>();

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