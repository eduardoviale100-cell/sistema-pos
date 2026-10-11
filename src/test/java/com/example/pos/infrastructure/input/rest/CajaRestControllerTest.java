package com.example.pos.infrastructure.input.rest;

import com.example.pos.application.usecase.*;
import com.example.pos.domain.model.CajaDomain;
import com.example.pos.domain.model.MovimientoCajaDomain;
import com.example.pos.domain.model.RolUsuario;
import com.example.pos.domain.model.SesionCajaDomain;
import com.example.pos.domain.model.UsuarioDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CajaRestControllerTest {

    @Mock
    private ListarCajasUseCase listarCajasUseCase;

    @Mock
    private CrearCajaUseCase crearCajaUseCase;

    @Mock
    private ActualizarCajaUseCase actualizarCajaUseCase;

    @Mock
    private EliminarCajaUseCase eliminarCajaUseCase;

    @Mock
    private AbrirSesionCajaUseCase abrirSesionCajaUseCase;

    @Mock
    private CerrarSesionCajaUseCase cerrarSesionCajaUseCase;

    @Mock
    private ObtenerSesionActivaUseCase obtenerSesionActivaUseCase;

    @Mock
    private RegistrarMovimientoCajaUseCase registrarMovimientoCajaUseCase;

    @Mock
    private ListarMovimientosCajaUseCase listarMovimientosCajaUseCase;

    @InjectMocks
    private CajaRestController cajaRestController;

    private CajaDomain caja;
    private UsuarioDomain usuario;
    private SesionCajaDomain sesion;

    @BeforeEach
    void setUp() {
        caja = new CajaDomain(1L, "Caja 1", "CERRADA");
        usuario = new UsuarioDomain(1L, "Cajero", "cajero1", "hash", RolUsuario.VENDEDOR, true, LocalDateTime.now(), null);
        sesion = SesionCajaDomain.iniciar(caja, usuario, new BigDecimal("100.00"), LocalDateTime.now());
        sesion.setId(10L);
    }

    @Test
    @DisplayName("GET /api/v2/cajas debe retornar 200 OK y la lista de cajas")
    void listarCajas_Exitoso() {
        when(listarCajasUseCase.ejecutar()).thenReturn(List.of(caja));

        ResponseEntity<List<CajaDomain>> response = cajaRestController.listarCajas();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(listarCajasUseCase).ejecutar();
    }

    @Test
    @DisplayName("POST /api/v2/cajas debe retornar 201 CREATED al crear una caja")
    void crearCaja_Exitoso() {
        CrearCajaInputDto dto = new CrearCajaInputDto("Caja 1");
        when(crearCajaUseCase.ejecutar("Caja 1")).thenReturn(caja);

        ResponseEntity<CajaDomain> response = cajaRestController.crearCaja(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Caja 1", response.getBody().getNombre());
        verify(crearCajaUseCase).ejecutar("Caja 1");
    }

    @Test
    @DisplayName("POST /api/v2/cajas/apertura debe retornar 201 CREATED al abrir sesión")
    void abrirSesion_Exitoso() {
        AperturaCajaInputDto dto = new AperturaCajaInputDto(1L, 1L, new BigDecimal("100.00"));
        when(abrirSesionCajaUseCase.ejecutar(1L, 1L, new BigDecimal("100.00"))).thenReturn(sesion);

        ResponseEntity<SesionCajaDomain> response = cajaRestController.abrirSesion(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ABIERTA", response.getBody().getEstado());
        verify(abrirSesionCajaUseCase).ejecutar(1L, 1L, new BigDecimal("100.00"));
    }

    @Test
    @DisplayName("POST /api/v2/cajas/sesiones/{id}/cierre debe retornar 200 OK")
    void cerrarSesion_Exitoso() {
        CierreCajaInputDto dto = new CierreCajaInputDto(new BigDecimal("150.00"));
        sesion.cerrar(new BigDecimal("150.00"), LocalDateTime.now());
        when(cerrarSesionCajaUseCase.ejecutar(10L, new BigDecimal("150.00"))).thenReturn(sesion);

        ResponseEntity<SesionCajaDomain> response = cajaRestController.cerrarSesion(10L, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CERRADA", response.getBody().getEstado());
        verify(cerrarSesionCajaUseCase).ejecutar(10L, new BigDecimal("150.00"));
    }

    @Test
    @DisplayName("GET /api/v2/cajas/{cajaId}/sesion-activa debe retornar 200 OK si existe")
    void obtenerSesionActiva_Exitoso() {
        when(obtenerSesionActivaUseCase.ejecutar(1L)).thenReturn(Optional.of(sesion));

        ResponseEntity<SesionCajaDomain> response = cajaRestController.obtenerSesionActiva(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(10L, response.getBody().getId());
    }

    @Test
    @DisplayName("POST /api/v2/cajas/sesiones/{id}/movimientos debe retornar 201 CREATED")
    void registrarMovimiento_Exitoso() {
        MovimientoCajaInputDto dto = MovimientoCajaInputDto.builder()
                .usuarioId(1L)
                .tipoMovimiento("INGRESO")
                .monto(new BigDecimal("50.00"))
                .referencia("Cambio")
                .observaciones("Ingreso inicial extra")
                .build();

        MovimientoCajaDomain mov = new MovimientoCajaDomain(1L, sesion, usuario, "INGRESO", new BigDecimal("50.00"), LocalDateTime.now(), "Cambio", "Ingreso inicial extra");
        when(registrarMovimientoCajaUseCase.ejecutar(10L, 1L, "INGRESO", new BigDecimal("50.00"), "Cambio", "Ingreso inicial extra")).thenReturn(mov);

        ResponseEntity<MovimientoCajaDomain> response = cajaRestController.registrarMovimiento(10L, dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INGRESO", response.getBody().getTipoMovimiento());
    }

    @Test
    @DisplayName("PUT /api/v2/cajas/{id} debe retornar 200 OK al actualizar una caja")
    void actualizarCaja_Exitoso() {
        CrearCajaInputDto dto = new CrearCajaInputDto("Caja Renovada");
        when(actualizarCajaUseCase.ejecutar(1L, "Caja Renovada")).thenReturn(caja);

        ResponseEntity<CajaDomain> response = cajaRestController.actualizarCaja(1L, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(actualizarCajaUseCase).ejecutar(1L, "Caja Renovada");
    }

    @Test
    @DisplayName("DELETE /api/v2/cajas/{id} debe retornar 204 NO_CONTENT al eliminar")
    void eliminarCaja_Exitoso() {
        doNothing().when(eliminarCajaUseCase).ejecutar(1L);

        ResponseEntity<Void> response = cajaRestController.eliminarCaja(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(eliminarCajaUseCase).ejecutar(1L);
    }
}
