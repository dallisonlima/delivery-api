package com.delivery_api.Projeto.Delivery.API.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ItemPedidoRequestDTO {

    @NotNull(message = "Produto é obrigatório")
    @Valid
    private IdRequestDTO produto;

    @NotNull(message = "Quantidade é obrigatória")
    @Positive(message = "Quantidade deve ser um valor positivo")
    private Integer quantidade;
}
