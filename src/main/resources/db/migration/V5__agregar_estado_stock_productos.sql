-- =====================================================================
-- V5__agregar_estado_stock_productos.sql
-- Añade las columnas 'estado' y 'stock' a la tabla productos para
-- soportar la baja lógica (soft delete) y el control de inventario
-- integrado en el módulo hexagonal de Productos.
-- =====================================================================

-- Columna de estado lógico (activo/inactivo) para soft delete
ALTER TABLE productos
    ADD COLUMN IF NOT EXISTS estado BOOLEAN NOT NULL DEFAULT TRUE;

-- Columna de stock actual para control de inventario en el módulo de Productos.
-- Nota: la tabla 'inventario' gestiona stock_actual y stock_minimo de forma
-- extendida; esta columna sirve al dominio hexagonal de Productos de forma directa.
ALTER TABLE productos
    ADD COLUMN IF NOT EXISTS stock INT NOT NULL DEFAULT 0
    CONSTRAINT chk_productos_stock_no_negativo CHECK (stock >= 0);
