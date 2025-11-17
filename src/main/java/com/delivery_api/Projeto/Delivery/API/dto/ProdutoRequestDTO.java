package com.delivery_api.Projeto.Delivery.API.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProdutoRequestDTO {

    @NotBlank(message = "Nome do produto é obrigatório")
    private String nome;

    @NotBlank(message = "Descrição do produto é obrigatória")
    private String descricao;

    @NotNull(message = "Preço do produto é obrigatório")
    @PositiveOrZero(message = "Preço do produto deve ser um valor positivo ou zero")
    private BigDecimal preco;

    @NotBlank(message = "Categoria do produto é obrigatória")
    private String categoria;

    @NotNull(message = "Disponibilidade do produto é obrigatória")
    private Boolean disponivel;

    @NotNull(message = "ID do restaurante é obrigatório")
    private Long restauranteId;
}
