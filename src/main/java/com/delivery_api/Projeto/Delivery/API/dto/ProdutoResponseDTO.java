package com.delivery_api.Projeto.Delivery.API.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProdutoResponseDTO {

    private Long id;

    private String nome;

    private String descricao;

    private BigDecimal preco;

    private String categoria;

    private Boolean disponivel;

    private Long restauranteId;

    private String nomeRestaurante;
}
