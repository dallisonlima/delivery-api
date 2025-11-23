package com.delivery_api.Projeto.Delivery.API.dto;

import com.delivery_api.Projeto.Delivery.API.enums.Role;

public record RegisterDTO(String email, String senha, String nome, Role role, Long restauranteId) {
}
