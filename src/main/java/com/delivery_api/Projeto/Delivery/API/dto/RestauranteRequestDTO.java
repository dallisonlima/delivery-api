package com.delivery_api.Projeto.Delivery.API.dto;

import com.delivery_api.Projeto.Delivery.API.validation.ValidCEP;
import com.delivery_api.Projeto.Delivery.API.validation.ValidCategoria;
import com.delivery_api.Projeto.Delivery.API.validation.ValidHorarioFuncionamento;
import com.delivery_api.Projeto.Delivery.API.validation.ValidTelefone;
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

    @NotBlank(message = "Horário de funcionamento é obrigatório")
    @ValidHorarioFuncionamento
    @Schema(description = "Horário de funcionamento", example = "18:00-23:00")
    private String horarioFuncionamento;

    @NotBlank(message = "Categoria é obrigatória")
    @ValidCategoria
    @Schema(description = "Categoria do restaurante", example = "Brasileira")
    private String categoria;

    @NotBlank(message = "Telefone é obrigatório")
    @ValidTelefone
    @Schema(description = "Telefone do restaurante (apenas números)", example = "11999998888")
    private String telefone;

    @NotBlank(message = "CEP é obrigatório")
    @ValidCEP
    @Schema(description = "CEP do restaurante", example = "12345-678")
    private String cep;

    @NotBlank(message = "Logradouro é obrigatório")
    @Schema(description = "Logradouro do endereço", example = "Rua das Flores")
    private String logradouro;

    @NotBlank(message = "Número é obrigatório")
    @Schema(description = "Número do endereço", example = "123")
    private String numero;

    @Schema(description = "Complemento do endereço", example = "Apto 101")
    private String complemento;

    @NotBlank(message = "Bairro é obrigatório")
    @Schema(description = "Bairro do endereço", example = "Centro")
    private String bairro;

    @NotBlank(message = "Cidade é obrigatória")
    @Schema(description = "Cidade do endereço", example = "São Paulo")
    private String cidade;

    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres")
    @Schema(description = "UF do estado", example = "SP")
    private String estado;
}
