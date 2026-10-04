package com.example.pos.application.usecase;

import com.example.pos.domain.model.RolUsuario;
import com.example.pos.domain.model.UsuarioDomain;
import com.example.pos.domain.repository.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Caso de Uso: Listar usuarios del sistema con filtros opcionales.
 */
@Service
public class ListarUsuariosUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public ListarUsuariosUseCase(UsuarioRepositoryPort usuarioRepositoryPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    /**
     * Retorna usuarios según los filtros especificados.
     *
     * @param soloActivos Si es {@code true}, retorna solo los usuarios activos.
     * @param rol         Si no es nulo, filtra por rol específico.
     * @return Lista de {@link UsuarioDomain}.
     */
    @Transactional(readOnly = true)
    public List<UsuarioDomain> ejecutar(boolean soloActivos, String rol) {
        if (rol != null && !rol.isBlank()) {
            RolUsuario rolFiltro = RolUsuario.fromString(rol);
            return usuarioRepositoryPort.listarPorRol(rolFiltro);
        }
        if (soloActivos) {
            return usuarioRepositoryPort.listarActivos();
        }
        return usuarioRepositoryPort.listarTodos();
    }
}
