package com.example.pos.service;

import com.example.pos.dto.ProveedorRequestDto;
import com.example.pos.model.Proveedor;
import com.example.pos.repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProveedorService {

    @Autowired
    private ProveedorRepository proveedorRepository;

    public List<Proveedor> listarTodos() {
        return proveedorRepository.findAll();
    }

    public Optional<Proveedor> buscarPorId(Integer id) {
        return proveedorRepository.findById(id);
    }

    @Transactional
    public Proveedor guardarProveedor(ProveedorRequestDto dto) {
        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(dto.getNombre());
        String ident = dto.getRucDni();
        if (ident == null || ident.isBlank()) {
            ident = "PRV-" + System.currentTimeMillis();
        }
        proveedor.setRucDni(ident);
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setEmail(dto.getEmail());
        proveedor.setDireccion(dto.getDireccion());
        return proveedorRepository.save(proveedor);
    }

    @Transactional
    public Proveedor actualizarProveedor(Integer id, ProveedorRequestDto dto) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + id));

        proveedor.setNombre(dto.getNombre().trim());
        String ident = dto.getRucDni();
        if (ident != null && !ident.isBlank()) {
            proveedor.setRucDni(ident.trim());
        }
        proveedor.setTelefono(dto.getTelefono() != null ? dto.getTelefono().trim() : null);
        proveedor.setEmail(dto.getEmail() != null ? dto.getEmail().trim() : null);
        proveedor.setDireccion(dto.getDireccion() != null ? dto.getDireccion().trim() : null);
        return proveedorRepository.save(proveedor);
    }

    @Transactional
    public void eliminarProveedor(Integer id) {
        proveedorRepository.deleteById(id);
    }
}
