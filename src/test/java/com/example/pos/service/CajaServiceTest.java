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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CajaServiceTest {

    @Mock
    private CajaRepository cajaRepository;
    @Mock
    private SesionCajaRepository sesionCajaRepository;
    @Mock
    private MovimientoCajaRepository movimientoCajaRepository;
    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CajaService cajaService;

    private Caja cajaCerrada;
    private Caja cajaAbierta;
    private Usuario usuarioActivo;
    private SesionCaja sesionAbierta;

    @BeforeEach
    void setUp() {
        cajaCerrada = new Caja();
        cajaCerrada.setId(1);
        cajaCerrada.setNombre("Caja 1");
        cajaCerrada.setEstado("CERRADA");

        cajaAbierta = new Caja();
        cajaAbierta.setId(2);
        cajaAbierta.setNombre("Caja 2");
        cajaAbierta.setEstado("ABIERTA");

        usuarioActivo = new Usuario();
        usuarioActivo.setId(1);
        usuarioActivo.setNombre("Admin POS");
        usuarioActivo.setActivo(true);

        sesionAbierta = new SesionCaja();
        sesionAbierta.setId(10);
        sesionAbierta.setCaja(cajaAbierta);
        sesionAbierta.setUsuario(usuarioActivo);
        sesionAbierta.setEstado("ABIERTA");
        sesionAbierta.setMontoInicial(new BigDecimal("100.00"));
        sesionAbierta.setMontoEsperado(new BigDecimal("250.00"));
    }

    @Test
    @DisplayName("Debe crear una nueva caja con estado inicial CERRADA")
    void crearCaja_DebeCrearConEstadoCerrada() {
        CajaRequestDto dto = new CajaRequestDto();
        dto.setNombre("Caja Secundaria");

        when(cajaRepository.save(any(Caja.class))).thenAnswer(i -> {
            Caja c = i.getArgument(0);
            c.setId(3);
            return c;
        });

        Caja resultado = cajaService.crearCaja(dto);

        assertNotNull(resultado);
        assertEquals(3, resultado.getId());
        assertEquals("Caja Secundaria", resultado.getNombre());
        assertEquals("CERRADA", resultado.getEstado());
        verify(cajaRepository).save(any(Caja.class));
    }

    @Test
    @DisplayName("Debe eliminar caja cuando no está ABIERTA")
    void eliminarCaja_CuandoEstaCerrada_DebeEliminar() {
        when(cajaRepository.findById(1)).thenReturn(Optional.of(cajaCerrada));
        doNothing().when(cajaRepository).deleteById(1);

        assertDoesNotThrow(() -> cajaService.eliminarCaja(1));
        verify(cajaRepository).deleteById(1);
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar eliminar caja con sesión ABIERTA")
    void eliminarCaja_CuandoEstaAbierta_LanzaExcepcion() {
        when(cajaRepository.findById(2)).thenReturn(Optional.of(cajaAbierta));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> cajaService.eliminarCaja(2));
        assertTrue(ex.getMessage().contains("No se puede eliminar una caja con sesión ABIERTA"));
        verify(cajaRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Debe abrir sesión correctamente")
    void abrirSesion_Exitosa() {
        AperturaCajaDto dto = new AperturaCajaDto();
        dto.setCajaId(1);
        dto.setUsuarioId(1);
        dto.setMontoInicial(new BigDecimal("150.00"));

        when(cajaRepository.findById(1)).thenReturn(Optional.of(cajaCerrada));
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioActivo));
        when(sesionCajaRepository.save(any(SesionCaja.class))).thenAnswer(i -> {
            SesionCaja s = i.getArgument(0);
            s.setId(20);
            return s;
        });

        SesionCaja sesion = cajaService.abrirSesion(dto);

        assertNotNull(sesion);
        assertEquals(20, sesion.getId());
        assertEquals("ABIERTA", sesion.getEstado());
        assertEquals(new BigDecimal("150.00"), sesion.getMontoInicial());
        assertEquals(new BigDecimal("150.00"), sesion.getMontoEsperado());
        assertEquals("ABIERTA", cajaCerrada.getEstado());
        verify(cajaRepository).save(cajaCerrada);
        verify(sesionCajaRepository).save(any(SesionCaja.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al abrir sesión si la caja ya está abierta")
    void abrirSesion_CajaYaAbierta_LanzaExcepcion() {
        AperturaCajaDto dto = new AperturaCajaDto();
        dto.setCajaId(2);
        dto.setUsuarioId(1);
        dto.setMontoInicial(new BigDecimal("100.00"));

        when(cajaRepository.findById(2)).thenReturn(Optional.of(cajaAbierta));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> cajaService.abrirSesion(dto));
        assertTrue(ex.getMessage().contains("ya tiene una sesión abierta"));
        verify(sesionCajaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe cerrar sesión calculando la diferencia y cerrando la caja")
    void cerrarSesion_Exitosa() {
        CierreCajaDto dto = new CierreCajaDto();
        dto.setMontoReal(new BigDecimal("260.00")); // Esperado era 250 -> Diferencia +10.00

        when(sesionCajaRepository.findById(10)).thenReturn(Optional.of(sesionAbierta));
        when(sesionCajaRepository.save(any(SesionCaja.class))).thenAnswer(i -> i.getArgument(0));

        SesionCaja cerrada = cajaService.cerrarSesion(10, dto);

        assertNotNull(cerrada);
        assertEquals("CERRADA", cerrada.getEstado());
        assertEquals(new BigDecimal("260.00"), cerrada.getMontoReal());
        assertEquals(new BigDecimal("10.00"), cerrada.getDiferencia());
        assertNotNull(cerrada.getFechaCierre());
        assertEquals("CERRADA", cajaAbierta.getEstado());
        verify(cajaRepository).save(cajaAbierta);
    }

    @Test
    @DisplayName("Debe lanzar excepción al cerrar sesión ya cerrada")
    void cerrarSesion_YaCerrada_LanzaExcepcion() {
        sesionAbierta.setEstado("CERRADA");
        when(sesionCajaRepository.findById(10)).thenReturn(Optional.of(sesionAbierta));

        CierreCajaDto dto = new CierreCajaDto();
        dto.setMontoReal(new BigDecimal("200.00"));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> cajaService.cerrarSesion(10, dto));
        assertTrue(ex.getMessage().contains("ya se encuentra CERRADA"));
    }

    @Test
    @DisplayName("Debe registrar un movimiento de INGRESO sumando al monto esperado")
    void registrarMovimiento_Ingreso_AumentaSaldo() {
        MovimientoCajaRequestDto dto = new MovimientoCajaRequestDto();
        dto.setUsuarioId(1);
        dto.setTipoMovimiento("INGRESO");
        dto.setMonto(new BigDecimal("50.00"));
        dto.setReferencia("Ingreso adicional de cambio");

        when(sesionCajaRepository.findById(10)).thenReturn(Optional.of(sesionAbierta));
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioActivo));
        when(movimientoCajaRepository.save(any(MovimientoCaja.class))).thenAnswer(i -> i.getArgument(0));

        MovimientoCaja mov = cajaService.registrarMovimiento(10, dto);

        assertNotNull(mov);
        assertEquals("INGRESO", mov.getTipoMovimiento());
        assertEquals(new BigDecimal("50.00"), mov.getMonto());
        assertEquals(new BigDecimal("300.00"), sesionAbierta.getMontoEsperado()); // 250 + 50
        verify(sesionCajaRepository).save(sesionAbierta);
    }

    @Test
    @DisplayName("Debe registrar un movimiento de RETIRO restando al monto esperado cuando hay saldo suficiente")
    void registrarMovimiento_Retiro_RestaSaldo() {
        MovimientoCajaRequestDto dto = new MovimientoCajaRequestDto();
        dto.setUsuarioId(1);
        dto.setTipoMovimiento("RETIRO");
        dto.setMonto(new BigDecimal("100.00"));
        dto.setReferencia("Pago a proveedor urgente");

        when(sesionCajaRepository.findById(10)).thenReturn(Optional.of(sesionAbierta));
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioActivo));
        when(movimientoCajaRepository.save(any(MovimientoCaja.class))).thenAnswer(i -> i.getArgument(0));

        MovimientoCaja mov = cajaService.registrarMovimiento(10, dto);

        assertNotNull(mov);
        assertEquals("RETIRO", mov.getTipoMovimiento());
        assertEquals(new BigDecimal("150.00"), sesionAbierta.getMontoEsperado()); // 250 - 100
        verify(sesionCajaRepository).save(sesionAbierta);
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException al intentar retirar más saldo del disponible")
    void registrarMovimiento_RetiroSaldoInsuficiente_LanzaExcepcion() {
        MovimientoCajaRequestDto dto = new MovimientoCajaRequestDto();
        dto.setUsuarioId(1);
        dto.setTipoMovimiento("RETIRO");
        dto.setMonto(new BigDecimal("500.00")); // Esperado es 250

        when(sesionCajaRepository.findById(10)).thenReturn(Optional.of(sesionAbierta));
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioActivo));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> cajaService.registrarMovimiento(10, dto));
        assertTrue(ex.getMessage().contains("Saldo insuficiente en caja para retiro"));
        verify(movimientoCajaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe listar movimientos de sesión")
    void listarMovimientosDeSesion_DebeRetornarLista() {
        when(movimientoCajaRepository.findBySesionCajaId(10)).thenReturn(List.of());

        List<MovimientoCaja> resultado = cajaService.listarMovimientosDeSesion(10);

        assertNotNull(resultado);
        verify(movimientoCajaRepository).findBySesionCajaId(10);
    }
}
