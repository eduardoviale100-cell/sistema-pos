-- =====================================================================
-- Migración V4: Agregar columna descripcion a categorias, marcas y productos
-- =====================================================================

ALTER TABLE categorias ADD COLUMN IF NOT EXISTS descripcion TEXT;
ALTER TABLE marcas ADD COLUMN IF NOT EXISTS descripcion TEXT;
ALTER TABLE productos ADD COLUMN IF NOT EXISTS descripcion TEXT;
