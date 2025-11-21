package com.delivery_api.Projeto.Delivery.API.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(name = "RestauranteRequestDTO", description = "DTO para criar ou atualizar um restaurante")
public class RestauranteRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    @Schema(description = "Nome do restaurante", example = "Restaurante do Zé")
    private String nome;

    @NotNull(message = "Taxa de entrega é obrigatória")
    @PositiveOrZero(message = "Taxa de entrega deve ser um valor positivo ou zero")
    @Schema(description = "Taxa de entrega do restaurante", example = "5.00")
    private BigDecimal taxaEntrega;

    @NotNull(message = "Tempo de entrega é obrigatório")
    @Min(value = 10, message = "Tempo de entrega deve ser de no mínimo 10 minutos")
    @Max(value = 120, message = "Tempo de entrega deve ser de no máximo 120 minutos")
    @Schema(description = "Tempo de entrega estimado em minutos", example = "30")
    private Integer tempoDeEntrega;

    @NotBlank(message = "Categoria é obrigatória")
    @Schema(description = "Categoria do restaurante", example = "Brasileira")
    private String categoria;

    @NotBlank(message = "Endereço é obrigatório")
    @Schema(description = "Endereço do restaurante", example = "Rua das Flores, 123, São Paulo, SP")
    private String endereco;

    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "^\\d{10,11}$", message = "Telefone deve ter 10 ou 11 dígitos")
    @Schema(description = "Telefone do restaurante (apenas números)", example = "11999998888")
    private String telefone;
}
