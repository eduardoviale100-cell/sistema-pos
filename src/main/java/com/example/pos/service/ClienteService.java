package com.example.pos.service;

import com.example.pos.dto.ClienteRequestDto;
import com.example.pos.model.Cliente;
import com.example.pos.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> buscarPorId(Integer id) {
        return clienteRepository.findById(id);
    }

    @Transactional
    public Cliente guardarCliente(ClienteRequestDto dto) {
        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre().trim());
        String doc = (dto.getDocumento() != null && !dto.getDocumento().isBlank()) ? dto.getDocumento().trim() : null;
        cliente.setDocumento(doc);
        cliente.setTelefono((dto.getTelefono() != null && !dto.getTelefono().isBlank()) ? dto.getTelefono().trim() : null);
        cliente.setEmail((dto.getEmail() != null && !dto.getEmail().isBlank()) ? dto.getEmail().trim() : null);
        cliente.setDireccion((dto.getDireccion() != null && !dto.getDireccion().isBlank()) ? dto.getDireccion().trim() : null);

        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente actualizarCliente(Integer id, ClienteRequestDto dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));

        cliente.setNombre(dto.getNombre().trim());
        String doc = (dto.getDocumento() != null && !dto.getDocumento().isBlank()) ? dto.getDocumento().trim() : null;
        cliente.setDocumento(doc);
        cliente.setTelefono((dto.getTelefono() != null && !dto.getTelefono().isBlank()) ? dto.getTelefono().trim() : null);
        cliente.setEmail((dto.getEmail() != null && !dto.getEmail().isBlank()) ? dto.getEmail().trim() : null);
        cliente.setDireccion((dto.getDireccion() != null && !dto.getDireccion().isBlank()) ? dto.getDireccion().trim() : null);

        return clienteRepository.save(cliente);
    }

    @Transactional
    public void eliminarCliente(Integer id) {
        clienteRepository.deleteById(id);
    }
}
