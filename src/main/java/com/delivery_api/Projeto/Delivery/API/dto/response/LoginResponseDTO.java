package com.delivery_api.Projeto.Delivery.API.dto.response;

import java.util.Date;

public record LoginResponseDTO(
        String token,
        String type,
        Date expiration,
        UserResponseDTO user
) {
    public LoginResponseDTO(String token, Date expiration, UserResponseDTO user) {
        this(token, "Bearer", expiration, user);
    }
}
