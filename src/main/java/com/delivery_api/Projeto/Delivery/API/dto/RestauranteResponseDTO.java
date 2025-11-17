package com.delivery_api.Projeto.Delivery.API.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RestauranteResponseDTO {

    private Long id;

    private String nome;

    private BigDecimal taxaEntrega;

    private String categoria;

    private Boolean ativo;

    private String endereco;

    private Double avaliacao;

    private String telefone;
}
