package com.example.pos.application.usecase;

import com.example.pos.domain.model.VentaDomain;
import com.example.pos.domain.repository.VentaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Caso de Uso: Listar ventas con soporte de filtros opcionales por sesión de caja y/o cajero.
 */
@Service
public class ListarVentasUseCase {

    private final VentaRepositoryPort ventaRepositoryPort;

    public ListarVentasUseCase(VentaRepositoryPort ventaRepositoryPort) {
        this.ventaRepositoryPort = ventaRepositoryPort;
    }

    @Transactional(readOnly = true)
    public List<VentaDomain> ejecutar(Long sesionCajaId, Long usuarioId) {
        if (sesionCajaId != null && usuarioId != null) {
            return ventaRepositoryPort.listarPorSesionCajaIdYUsuarioId(sesionCajaId, usuarioId);
        } else if (sesionCajaId != null) {
            return ventaRepositoryPort.listarPorSesionCajaId(sesionCajaId);
        } else if (usuarioId != null) {
            return ventaRepositoryPort.listarPorUsuarioId(usuarioId);
        }
        return ventaRepositoryPort.listarTodas();
    }
}
