-- ==========================================================================
-- V7__insertar_usuario_admin_inicial.sql
-- Inserta el usuario administrador por defecto del sistema POS.
-- Credenciales: admin / admin123
-- Hash: BCryptPasswordEncoder (coste 10)
-- IDEMPOTENTE: no genera error si el usuario ya existe.
-- ==========================================================================

INSERT INTO usuarios (nombre, usuario, contrasena, rol, activo)
VALUES (
    'Administrador del Sistema',
    'admin',
    '$2a$10$QLQESdNk6aGLk07mdZgQd.enKwsGNBArsHxqmiJE7DFjTuTOTOvHG',
    'ADMIN',
    TRUE
)
ON CONFLICT (usuario) DO NOTHING;
