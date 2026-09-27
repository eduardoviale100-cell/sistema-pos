package com.example.pos.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Debe manejar IllegalArgumentException con status 400")
    void handleIllegalArgumentException_DebeRetornar400() {
        IllegalArgumentException ex = new IllegalArgumentException("Parámetro inválido");

        ResponseEntity<Map<String, Object>> response = handler.handleIllegalArgumentException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Petición Inválida", response.getBody().get("error"));
        assertEquals("Parámetro inválido", response.getBody().get("mensaje"));
    }

    @Test
    @DisplayName("Debe manejar IllegalStateException con status 409 CONFLICT")
    void handleIllegalStateException_DebeRetornar409() {
        IllegalStateException ex = new IllegalStateException("Caja ya abierta");

        ResponseEntity<Map<String, Object>> response = handler.handleIllegalStateException(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Conflicto de Estado de Negocio", response.getBody().get("error"));
        assertEquals("Caja ya abierta", response.getBody().get("mensaje"));
    }

    @Test
    @DisplayName("Debe manejar DataIntegrityViolationException con clave duplicada retornando 409")
    void handleDataIntegrityViolation_DuplicateKey_DebeRetornar409() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("duplicate key value violates unique constraint");

        ResponseEntity<Map<String, Object>> response = handler.handleDataIntegrityViolation(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Conflicto de Integridad de Datos", response.getBody().get("error"));
        assertTrue(response.getBody().get("mensaje").toString().contains("Ya existe un registro"));
    }

    @Test
    @DisplayName("Debe manejar RuntimeException con status 400")
    void handleRuntimeException_DebeRetornar400() {
        RuntimeException ex = new RuntimeException("Error genérico de negocio");

        ResponseEntity<Map<String, Object>> response = handler.handleRuntimeException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error en la Operación", response.getBody().get("error"));
        assertEquals("Error genérico de negocio", response.getBody().get("mensaje"));
    }

    @Test
    @DisplayName("Debe manejar Exception general con status 500")
    void handleGeneralException_DebeRetornar500() {
        Exception ex = new Exception("Error imprevisto en base de datos");

        ResponseEntity<Map<String, Object>> response = handler.handleGeneralException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error Interno del Servidor", response.getBody().get("error"));
    }
}
