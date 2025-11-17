package com.delivery_api.Projeto.Delivery.API.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(name = "RestauranteRequestDTO", description = "DTO para criar ou atualizar um restaurante")
public class RestauranteRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Schema(description = "Nome do restaurante", example = "Restaurante do Zé")
    private String nome;

    @NotNull(message = "Taxa de entrega é obrigatória")
    @PositiveOrZero(message = "Taxa de entrega deve ser um valor positivo ou zero")
    @Schema(description = "Taxa de entrega do restaurante", example = "5.00")
    private BigDecimal taxaEntrega;

    @NotBlank(message = "Categoria é obrigatória")
    @Schema(description = "Categoria do restaurante", example = "Brasileira")
    private String categoria;

    @NotBlank(message = "Endereço é obrigatório")
    @Schema(description = "Endereço do restaurante", example = "Rua das Flores, 123, São Paulo, SP")
    private String endereco;

    @Schema(description = "Telefone do restaurante", example = "(11) 99999-9999")
    private String telefone;
}
