package com.example.pos.service;

import com.example.pos.dto.AperturaCajaDto;
import com.example.pos.dto.CajaRequestDto;
import com.example.pos.dto.CierreCajaDto;
import com.example.pos.dto.MovimientoCajaRequestDto;
import com.example.pos.model.Caja;
import com.example.pos.model.MovimientoCaja;
import com.example.pos.model.SesionCaja;
import com.example.pos.model.Usuario;
import com.example.pos.repository.CajaRepository;
import com.example.pos.repository.MovimientoCajaRepository;
import com.example.pos.repository.SesionCajaRepository;
import com.example.pos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CajaService {

    @Autowired
    private CajaRepository cajaRepository;

    @Autowired
    private SesionCajaRepository sesionCajaRepository;

    @Autowired
    private MovimientoCajaRepository movimientoCajaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Caja> listarCajas() {
        return cajaRepository.findAll();
    }

    public Optional<Caja> buscarCajaPorId(Integer id) {
        return cajaRepository.findById(id);
    }

    @Transactional
    public Caja crearCaja(CajaRequestDto dto) {
        Caja caja = new Caja();
        caja.setNombre(dto.getNombre());
        caja.setEstado("CERRADA");
        return cajaRepository.save(caja);
    }

    @Transactional
    public Caja actualizarCaja(Integer id, CajaRequestDto dto) {
        Caja caja = cajaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Caja no encontrada con ID: " + id));
        caja.setNombre(dto.getNombre().trim());
        return cajaRepository.save(caja);
    }

    @Transactional
    public void eliminarCaja(Integer id) {
        Caja caja = cajaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Caja no encontrada con ID: " + id));
        if ("ABIERTA".equalsIgnoreCase(caja.getEstado())) {
            throw new IllegalStateException("No se puede eliminar una caja con sesión ABIERTA.");
        }
        cajaRepository.deleteById(id);
    }

    @Transactional
    public SesionCaja abrirSesion(AperturaCajaDto dto) {
        Caja caja = cajaRepository.findById(dto.getCajaId())
                .orElseThrow(() -> new RuntimeException("Caja no encontrada con ID: " + dto.getCajaId()));

        if ("ABIERTA".equalsIgnoreCase(caja.getEstado())) {
            throw new IllegalStateException("La caja '" + caja.getNombre() + "' ya tiene una sesión abierta.");
        }

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + dto.getUsuarioId()));

        if (Boolean.FALSE.equals(usuario.getActivo())) {
            throw new IllegalStateException("El usuario '" + usuario.getNombre() + "' no se encuentra activo.");
        }

        caja.setEstado("ABIERTA");
        cajaRepository.save(caja);

        SesionCaja sesion = new SesionCaja();
        sesion.setCaja(caja);
        sesion.setUsuario(usuario);
        sesion.setFechaApertura(LocalDateTime.now());
        sesion.setMontoInicial(dto.getMontoInicial());
        sesion.setMontoEsperado(dto.getMontoInicial());
        sesion.setEstado("ABIERTA");

        return sesionCajaRepository.save(sesion);
    }

    @Transactional
    public SesionCaja cerrarSesion(Integer sesionId, CierreCajaDto dto) {
        SesionCaja sesion = sesionCajaRepository.findById(sesionId)
                .orElseThrow(() -> new RuntimeException("Sesión de caja no encontrada con ID: " + sesionId));

        if ("CERRADA".equalsIgnoreCase(sesion.getEstado())) {
            throw new IllegalStateException("La sesión de caja ya se encuentra CERRADA.");
        }

        BigDecimal esperado = sesion.getMontoEsperado() != null ? sesion.getMontoEsperado() : sesion.getMontoInicial();
        BigDecimal diferencia = dto.getMontoReal().subtract(esperado);

        sesion.setFechaCierre(LocalDateTime.now());
        sesion.setMontoReal(dto.getMontoReal());
        sesion.setDiferencia(diferencia);
        sesion.setEstado("CERRADA");

        Caja caja = sesion.getCaja();
        caja.setEstado("CERRADA");
        cajaRepository.save(caja);

        return sesionCajaRepository.save(sesion);
    }

    @Transactional
    public MovimientoCaja registrarMovimiento(Integer sesionId, MovimientoCajaRequestDto dto) {
        SesionCaja sesion = sesionCajaRepository.findById(sesionId)
                .orElseThrow(() -> new RuntimeException("Sesión de caja no encontrada con ID: " + sesionId));

        if (!"ABIERTA".equalsIgnoreCase(sesion.getEstado())) {
            throw new IllegalStateException("No se pueden registrar movimientos en una sesión que no esté ABIERTA.");
        }

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + dto.getUsuarioId()));

        String tipo = dto.getTipoMovimiento().toUpperCase();
        if ("EGRESO".equals(tipo)) {
            tipo = "RETIRO";
        }
        BigDecimal monto = dto.getMonto();
        BigDecimal saldoActual = sesion.getMontoEsperado() != null ? sesion.getMontoEsperado() : sesion.getMontoInicial();

        if ("INGRESO".equals(tipo)) {
            sesion.setMontoEsperado(saldoActual.add(monto));
        } else if ("RETIRO".equals(tipo)) {
            if (saldoActual.compareTo(monto) < 0) {
                throw new IllegalArgumentException(
                        String.format("Saldo insuficiente en caja para retiro. Saldo en caja: %s, Solicitado: %s",
                                saldoActual, monto));
            }
            sesion.setMontoEsperado(saldoActual.subtract(monto));
        } else {
            throw new IllegalArgumentException("Tipo de movimiento inválido: " + dto.getTipoMovimiento());
        }

        sesionCajaRepository.save(sesion);

        MovimientoCaja movimiento = new MovimientoCaja();
        movimiento.setSesionCaja(sesion);
        movimiento.setUsuario(usuario);
        movimiento.setTipoMovimiento(tipo);
        movimiento.setMonto(monto);
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setReferencia(dto.getReferencia());
        movimiento.setObservaciones(dto.getObservaciones());

        return movimientoCajaRepository.save(movimiento);
    }

    public Optional<SesionCaja> obtenerSesionActiva(Integer cajaId) {
        return sesionCajaRepository.findByCajaIdAndEstado(cajaId, "ABIERTA");
    }

    public List<MovimientoCaja> listarMovimientosDeSesion(Integer sesionId) {
        return movimientoCajaRepository.findBySesionCajaId(sesionId);
    }
}
