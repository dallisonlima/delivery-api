package com.delivery_api.Projeto.Delivery.API.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RestauranteRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotNull(message = "Taxa de entrega é obrigatória")
    @PositiveOrZero(message = "Taxa de entrega deve ser um valor positivo ou zero")
    private BigDecimal taxaEntrega;

    @NotBlank(message = "Categoria é obrigatória")
    private String categoria;

    @NotBlank(message = "Endereço é obrigatório")
    private String endereco;

    private String telefone;
}
