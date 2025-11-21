package com.delivery_api.Projeto.Delivery.API.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(name = "RestauranteResponseDTO", description = "DTO para exibir um restaurante")
public class RestauranteResponseDTO {

    @Schema(description = "ID do restaurante", example = "1")
    private Long id;

    @Schema(description = "Nome do restaurante", example = "Restaurante do Zé")
    private String nome;

    @Schema(description = "Taxa de entrega do restaurante", example = "5.00")
    private BigDecimal taxaEntrega;

    @Schema(description = "Tempo de entrega estimado em minutos", example = "30")
    private Integer tempoDeEntrega;

    @Schema(description = "Horário de funcionamento", example = "18:00-23:00")
    private String horarioFuncionamento;

    @Schema(description = "Categoria do restaurante", example = "Brasileira")
    private String categoria;

    @Schema(description = "Indica se o restaurante está ativo", example = "true")
    private Boolean ativo;

    @Schema(description = "Endereço do restaurante")
    private EnderecoDTO endereco;

    @Schema(description = "Avaliação do restaurante", example = "4.5")
    private Double avaliacao;

    @Schema(description = "Telefone do restaurante", example = "(11) 99999-9999")
    private String telefone;
}
