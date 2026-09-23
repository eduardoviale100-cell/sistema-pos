package com.example.pos.service;

import com.example.pos.dto.UsuarioRequestDto;
import com.example.pos.model.Usuario;
import com.example.pos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Integer id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorUsuario(String usuario) {
        return usuarioRepository.findByUsuario(usuario);
    }

    @Transactional
    public Usuario guardarUsuario(UsuarioRequestDto dto) {
        if (usuarioRepository.existsByUsuario(dto.getUsuario())) {
            throw new IllegalArgumentException("El nombre de usuario '" + dto.getUsuario() + "' ya está registrado.");
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(dto.getNombre());
        nuevoUsuario.setUsuario(dto.getUsuario());
        nuevoUsuario.setPassword(dto.getPassword());
        nuevoUsuario.setRol(dto.getRol() != null ? dto.getRol().toUpperCase() : "VENDEDOR");
        nuevoUsuario.setActivo(dto.getActivo() != null ? dto.getActivo() : true);

        return usuarioRepository.save(nuevoUsuario);
    }

    @Transactional
    public Usuario cambiarEstado(Integer id, boolean activo) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        usuario.setActivo(activo);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario actualizarUsuario(Integer id, UsuarioRequestDto dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // Check if username is changed and already taken by someone else
        if (!usuario.getUsuario().equalsIgnoreCase(dto.getUsuario()) && usuarioRepository.existsByUsuario(dto.getUsuario())) {
            throw new IllegalArgumentException("El nombre de usuario '" + dto.getUsuario() + "' ya está ocupado.");
        }

        usuario.setNombre(dto.getNombre().trim());
        usuario.setUsuario(dto.getUsuario().trim());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            usuario.setPassword(dto.getPassword());
        }
        if (dto.getRol() != null && !dto.getRol().isBlank()) {
            usuario.setRol(dto.getRol().toUpperCase().trim());
        }
        if (dto.getActivo() != null) {
            usuario.setActivo(dto.getActivo());
        }

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void eliminarUsuario(Integer id) {
        usuarioRepository.deleteById(id);
    }
}
