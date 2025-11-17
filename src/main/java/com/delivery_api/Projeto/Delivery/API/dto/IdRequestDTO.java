package com.delivery_api.Projeto.Delivery.API.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class IdRequestDTO {
    @NotNull(message = "ID é obrigatório")
    @Positive(message = "ID deve ser um valor positivo")
    private Long id;
}
