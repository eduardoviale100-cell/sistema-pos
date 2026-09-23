-- =====================================================================
-- Insertar Métodos de Pago Iniciales (Efectivo, Tarjeta, Yape, Plin, Transferencia)
-- V2__insertar_metodos_pago_iniciales.sql
-- =====================================================================

INSERT INTO metodos_pago (nombre, activo) VALUES
('Efectivo', true),
('Tarjeta', true),
('Yape', true),
('Plin', true),
('Transferencia', true)
ON CONFLICT (nombre) DO NOTHING;
