package com.delivery_api.Projeto.Delivery.API.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(name = "ProdutoRequestDTO", description = "DTO para criar ou atualizar um produto")
public class ProdutoRequestDTO {

    @NotBlank(message = "Nome do produto é obrigatório")
    @Schema(description = "Nome do produto", example = "Pizza de Calabresa")
    private String nome;

    @NotBlank(message = "Descrição do produto é obrigatória")
    @Schema(description = "Descrição do produto", example = "Pizza de calabresa com queijo e orégano")
    private String descricao;

    @NotNull(message = "Preço do produto é obrigatório")
    @PositiveOrZero(message = "Preço do produto deve ser um valor positivo ou zero")
    @Schema(description = "Preço do produto", example = "50.00")
    private BigDecimal preco;

    @NotBlank(message = "Categoria do produto é obrigatória")
    @Schema(description = "Categoria do produto", example = "Pizzas")
    private String categoria;

    @NotNull(message = "Disponibilidade do produto é obrigatória")
    @Schema(description = "Indica se o produto está disponível", example = "true")
    private Boolean disponivel;

    @NotNull(message = "ID do restaurante é obrigatório")
    @Schema(description = "ID do restaurante ao qual o produto pertence", example = "1")
    private Long restauranteId;
}
