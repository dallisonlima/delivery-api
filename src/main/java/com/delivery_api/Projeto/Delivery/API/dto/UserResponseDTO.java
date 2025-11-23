package com.delivery_api.Projeto.Delivery.API.dto;

import com.delivery_api.Projeto.Delivery.API.entity.Usuario;
import com.delivery_api.Projeto.Delivery.API.enums.Role;

public record UserResponseDTO(
        Long id,
        String nome,
        String email,
        Role role
) {
    public UserResponseDTO(Usuario usuario) {
        this(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getRole());
    }
}
