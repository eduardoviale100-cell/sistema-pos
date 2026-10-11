package com.example.pos.application.usecase;

import com.example.pos.domain.exception.CajaNoEncontradaException;
import com.example.pos.domain.exception.CajaYaAbiertaException;
import com.example.pos.domain.exception.SaldoInsuficienteCajaException;
import com.example.pos.domain.exception.SesionCajaNoAbiertaException;
import com.example.pos.domain.model.CajaDomain;
import com.example.pos.domain.model.MovimientoCajaDomain;
import com.example.pos.domain.model.RolUsuario;
import com.example.pos.domain.model.SesionCajaDomain;
import com.example.pos.domain.model.UsuarioDomain;
import com.example.pos.domain.repository.CajaRepositoryPort;
import com.example.pos.domain.repository.MovimientoCajaRepositoryPort;
import com.example.pos.domain.repository.SesionCajaRepositoryPort;
import com.example.pos.domain.repository.UsuarioRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CajaUseCaseTest {

    @Mock
    private CajaRepositoryPort cajaRepositoryPort;

    @Mock
    private SesionCajaRepositoryPort sesionCajaRepositoryPort;

    @Mock
    private MovimientoCajaRepositoryPort movimientoCajaRepositoryPort;

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    private AbrirSesionCajaUseCase abrirSesionCajaUseCase;
    private CerrarSesionCajaUseCase cerrarSesionCajaUseCase;
    private RegistrarMovimientoCajaUseCase registrarMovimientoCajaUseCase;
    private ObtenerSesionActivaUseCase obtenerSesionActivaUseCase;

    private CajaDomain cajaCerrada;
    private CajaDomain cajaAbierta;
    private UsuarioDomain usuarioActivo;

    @BeforeEach
    void setUp() {
        abrirSesionCajaUseCase = new AbrirSesionCajaUseCase(cajaRepositoryPort, sesionCajaRepositoryPort, usuarioRepositoryPort);
        cerrarSesionCajaUseCase = new CerrarSesionCajaUseCase(sesionCajaRepositoryPort, cajaRepositoryPort);
        registrarMovimientoCajaUseCase = new RegistrarMovimientoCajaUseCase(sesionCajaRepositoryPort, movimientoCajaRepositoryPort, usuarioRepositoryPort);
        obtenerSesionActivaUseCase = new ObtenerSesionActivaUseCase(sesionCajaRepositoryPort);

        cajaCerrada = new CajaDomain(1L, "Caja Principal", "CERRADA");
        cajaAbierta = new CajaDomain(2L, "Caja Secundaria", "ABIERTA");
        usuarioActivo = new UsuarioDomain(1L, "Admin", "admin", "hash", RolUsuario.ADMIN, true, LocalDateTime.now(), null);
    }

    @Test
    @DisplayName("Abrir sesión exitosamente")
    void abrirSesion_Exitoso() {
        when(cajaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(cajaCerrada));
        when(sesionCajaRepositoryPort.existeSesionAbierta(1L)).thenReturn(false);
        when(usuarioRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(usuarioActivo));
        when(cajaRepositoryPort.guardar(any(CajaDomain.class))).thenAnswer(i -> i.getArgument(0));
        when(sesionCajaRepositoryPort.guardar(any(SesionCajaDomain.class))).thenAnswer(i -> i.getArgument(0));

        SesionCajaDomain sesion = abrirSesionCajaUseCase.ejecutar(1L, 1L, new BigDecimal("100.00"));

        assertNotNull(sesion);
        assertEquals("ABIERTA", sesion.getEstado());
        assertEquals(new BigDecimal("100.00"), sesion.getMontoInicial());
        assertEquals(new BigDecimal("100.00"), sesion.getMontoEsperado());
        assertTrue(cajaCerrada.isAbierta());
        verify(cajaRepositoryPort).guardar(cajaCerrada);
        verify(sesionCajaRepositoryPort).guardar(any(SesionCajaDomain.class));
    }

    @Test
    @DisplayName("Lanzar excepción al intentar abrir caja que ya está abierta")
    void abrirSesion_CajaYaAbierta_LanzaExcepcion() {
        when(cajaRepositoryPort.buscarPorId(2L)).thenReturn(Optional.of(cajaAbierta));

        assertThrows(CajaYaAbiertaException.class, () ->
                abrirSesionCajaUseCase.ejecutar(2L, 1L, new BigDecimal("50.00"))
        );
    }

    @Test
    @DisplayName("Cerrar sesión exitosamente calculando arqueo y diferencia")
    void cerrarSesion_Exitoso() {
        SesionCajaDomain sesion = SesionCajaDomain.iniciar(cajaAbierta, usuarioActivo, new BigDecimal("100.00"), LocalDateTime.now());
        sesion.setId(10L);

        when(sesionCajaRepositoryPort.buscarPorId(10L)).thenReturn(Optional.of(sesion));
        when(sesionCajaRepositoryPort.guardar(any(SesionCajaDomain.class))).thenAnswer(i -> i.getArgument(0));
        when(cajaRepositoryPort.guardar(any(CajaDomain.class))).thenAnswer(i -> i.getArgument(0));

        SesionCajaDomain cerrada = cerrarSesionCajaUseCase.ejecutar(10L, new BigDecimal("120.00"));

        assertEquals("CERRADA", cerrada.getEstado());
        assertEquals(new BigDecimal("120.00"), cerrada.getMontoReal());
        assertEquals(new BigDecimal("20.00"), cerrada.getDiferencia());
        assertFalse(cajaAbierta.isAbierta());
    }

    @Test
    @DisplayName("Registrar ingreso manual incrementa el monto esperado")
    void registrarMovimiento_Ingreso_Exitoso() {
        SesionCajaDomain sesion = SesionCajaDomain.iniciar(cajaAbierta, usuarioActivo, new BigDecimal("100.00"), LocalDateTime.now());
        sesion.setId(10L);

        when(sesionCajaRepositoryPort.buscarPorId(10L)).thenReturn(Optional.of(sesion));
        when(usuarioRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(usuarioActivo));
        when(sesionCajaRepositoryPort.guardar(any(SesionCajaDomain.class))).thenAnswer(i -> i.getArgument(0));
        when(movimientoCajaRepositoryPort.guardar(any(MovimientoCajaDomain.class))).thenAnswer(i -> i.getArgument(0));

        MovimientoCajaDomain mov = registrarMovimientoCajaUseCase.ejecutar(
                10L, 1L, "INGRESO", new BigDecimal("50.00"), "Cambio adicional", "Observación"
        );

        assertNotNull(mov);
        assertEquals("INGRESO", mov.getTipoMovimiento());
        assertEquals(new BigDecimal("150.00"), sesion.getMontoEsperado());
    }

    @Test
    @DisplayName("Lanzar excepción al intentar retirar más dinero del disponible en caja")
    void registrarMovimiento_Retiro_SaldoInsuficiente_LanzaExcepcion() {
        SesionCajaDomain sesion = SesionCajaDomain.iniciar(cajaAbierta, usuarioActivo, new BigDecimal("100.00"), LocalDateTime.now());
        sesion.setId(10L);

        when(sesionCajaRepositoryPort.buscarPorId(10L)).thenReturn(Optional.of(sesion));
        when(usuarioRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(usuarioActivo));

        assertThrows(SaldoInsuficienteCajaException.class, () ->
                registrarMovimientoCajaUseCase.ejecutar(10L, 1L, "RETIRO", new BigDecimal("200.00"), "Pago servicios", null)
        );
    }
}
