package com.example.pos.service;

import com.example.pos.dto.MarcaRequestDto;
import com.example.pos.model.Marca;
import com.example.pos.repository.MarcaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MarcaService {

    @Autowired
    private MarcaRepository marcaRepository;

    public List<Marca> listarTodos() {
        return marcaRepository.findAll();
    }

    public Optional<Marca> buscarPorId(Integer id) {
        return marcaRepository.findById(id);
    }

    @Transactional
    public Marca guardarMarca(MarcaRequestDto dto) {
        Marca marca = new Marca();
        marca.setNombre(dto.getNombre());
        marca.setDescripcion(dto.getDescripcion());
        return marcaRepository.save(marca);
    }

    @Transactional
    public Marca actualizarMarca(Integer id, MarcaRequestDto dto) {
        Marca marca = marcaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Marca no encontrada con ID: " + id));
        marca.setNombre(dto.getNombre());
        marca.setDescripcion(dto.getDescripcion());
        return marcaRepository.save(marca);
    }

    @Transactional
    public void eliminarMarca(Integer id) {
        marcaRepository.deleteById(id);
    }
}
