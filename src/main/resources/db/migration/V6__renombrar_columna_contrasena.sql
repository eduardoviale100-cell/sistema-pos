-- ==========================================================================
-- V6__renombrar_columna_contrasena.sql
-- Renombra la columna con tilde a 'contrasena' (ASCII puro).
-- IDEMPOTENTE: no falla si la columna ya fue renombrada.
-- ==========================================================================

DO $$
DECLARE
    v_col_name TEXT;
BEGIN
    SELECT column_name
    INTO   v_col_name
    FROM   information_schema.columns
    WHERE  table_schema = 'public'
      AND  table_name   = 'usuarios'
      AND  column_name  = 'contraseña'
    LIMIT 1;

    IF v_col_name IS NOT NULL THEN
        EXECUTE format('ALTER TABLE usuarios RENAME COLUMN %I TO contrasena', v_col_name);
        RAISE NOTICE 'OK: Columna renombrada a contrasena';
    ELSE
        RAISE NOTICE 'INFO: La columna ya es contrasena o no existe la columna con tilde.';
    END IF;
END;
$$;
