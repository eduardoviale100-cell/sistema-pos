package com.example.pos.infrastructure.output.persistence.inventario;


import com.example.pos.infrastructure.output.persistence.producto.ProductoEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad JPA para la tabla 'inventario' en la capa de persistencia.
 */
@Entity
@Table(name = "inventario")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false, unique = true)
    private ProductoEntity producto;

    @Column(name = "stock_actual", nullable = false)
    @Builder.Default
    private Integer stockActual = 0;

    @Column(name = "stock_minimo", nullable = false)
    @Builder.Default
    private Integer stockMinimo = 5;

    @Column(name = "ultima_actualizacion")
    private LocalDateTime ultimaActualizacion;

    @PrePersist
    @PreUpdate
    public void prePersistUpdate() {
        this.ultimaActualizacion = LocalDateTime.now();
        if (this.stockActual == null) {
            this.stockActual = 0;
        }
        if (this.stockMinimo == null) {
            this.stockMinimo = 5;
        }
    }
}

