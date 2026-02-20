package com.example.Consultoria.TI.modelo;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "servicio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long idServicio;

    private String moneda;
    private String numeroOrden;
    private LocalDateTime fechaHoraAgendamiento;
    private LocalDateTime fechaCreacion;
    private Integer tiempoEstimado;
    private Double presupuesto;
    private String estado;
    private String promocion;
    private String descuento;
    private String reclamo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCliente", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idTecnico")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Tecnico tecnicoAsignado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idTipoServicio", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private TipoServicio tipoServicio;

    @OneToMany(mappedBy = "servicio", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<DetalleServicio> detalles = new LinkedHashSet<>();

    @OneToMany(mappedBy = "servicio", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
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
    private Double costoManoObra;
}