-- Agrega columna codigo_factura a la tabla ventas para folios legibles
ALTER TABLE ventas ADD COLUMN IF NOT EXISTS codigo_factura VARCHAR(30) UNIQUE;

-- Genera folios retroactivos para ventas existentes que no tengan código
UPDATE ventas
SET codigo_factura = CONCAT('VTA-', TO_CHAR(fecha, 'YYYYMMDD'), '-', LPAD(id::text, 5, '0'))
WHERE codigo_factura IS NULL;
