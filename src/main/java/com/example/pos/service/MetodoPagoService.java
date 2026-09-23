package com.example.pos.service;

import com.example.pos.dto.MetodoPagoRequestDto;
import com.example.pos.model.MetodoPago;
import com.example.pos.repository.MetodoPagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import jakarta.annotation.PostConstruct;

@Service
public class MetodoPagoService {

    @Autowired
    private MetodoPagoRepository metodoPagoRepository;

    @PostConstruct
    public void initData() {
        if (metodoPagoRepository.count() == 0) {
            String[] metodosIniciales = {"Efectivo", "Tarjeta", "Yape", "Plin", "Transferencia"};
            for (String nombre : metodosIniciales) {
                MetodoPago m = new MetodoPago();
                m.setNombre(nombre);
                m.setActivo(true);
                try {
                    metodoPagoRepository.save(m);
                } catch (Exception ignored) {}
            }
        }
    }

    public List<MetodoPago> listarTodos() {
        return metodoPagoRepository.findAll();
    }

    public List<MetodoPago> listarActivos() {
        return metodoPagoRepository.findByActivoTrue();
    }

    public Optional<MetodoPago> buscarPorId(Integer id) {
        return metodoPagoRepository.findById(id);
    }

    @Transactional
    public MetodoPago guardarMetodoPago(MetodoPagoRequestDto dto) {
        MetodoPago metodo = new MetodoPago();
        metodo.setNombre(dto.getNombre());
        metodo.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        return metodoPagoRepository.save(metodo);
    }

    @Transactional
    public MetodoPago cambiarEstado(Integer id, boolean activo) {
        MetodoPago metodo = metodoPagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Método de pago no encontrado con ID: " + id));
        metodo.setActivo(activo);
        return metodoPagoRepository.save(metodo);
    }

    @Transactional
    public MetodoPago actualizarMetodoPago(Integer id, MetodoPagoRequestDto dto) {
        MetodoPago metodo = metodoPagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Método de pago no encontrado con ID: " + id));
        metodo.setNombre(dto.getNombre().trim());
        if (dto.getActivo() != null) {
            metodo.setActivo(dto.getActivo());
        }
        return metodoPagoRepository.save(metodo);
    }

    @Transactional
    public void eliminarMetodoPago(Integer id) {
        metodoPagoRepository.deleteById(id);
    }
}
