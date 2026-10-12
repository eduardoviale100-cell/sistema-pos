package com.example.pos.infrastructure.output.persistence.venta;


import com.example.pos.infrastructure.output.persistence.usuario.UsuarioEntity;
import com.example.pos.infrastructure.output.persistence.cliente.ClienteEntity;
import com.example.pos.infrastructure.output.persistence.caja.SesionCajaEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Entidad JPA para la tabla 'ventas' (Capa de Infraestructura).
 */
@Entity
@Table(name = "ventas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private ClienteEntity cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sesion_caja_id", nullable = false)
    private SesionCajaEntity sesionCaja;

    @Column(name = "codigo_factura", unique = true, length = 30)
    private String codigoFactura;

    private LocalDateTime fecha;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<VentaDetalleEntity> detalles = new LinkedHashSet<>();

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<PagoEntity> pagos = new LinkedHashSet<>();

    @PrePersist
    public void prePersist() {
        if (this.fecha == null) {
            this.fecha = LocalDateTime.now();
        }
        if (this.total == null) {
            this.total = BigDecimal.ZERO;
        }
    }
}

