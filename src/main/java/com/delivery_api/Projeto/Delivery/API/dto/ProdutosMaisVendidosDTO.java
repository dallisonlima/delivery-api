package com.delivery_api.Projeto.Delivery.API.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ProdutosMaisVendidosDTO", description = "DTO para exibir os produtos mais vendidos")
public class ProdutosMaisVendidosDTO {

    @Schema(description = "Nome do produto", example = "Pizza de Calabresa")
    private String nomeProduto;

    @Schema(description = "Quantidade vendida do produto", example = "50")
    private Long quantidadeVendida;

    public ProdutosMaisVendidosDTO(String nomeProduto, Long quantidadeVendida) {
        this.nomeProduto = nomeProduto;
        this.quantidadeVendida = quantidadeVendida;
    }

    // Getters and Setters
    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public Long getQuantidadeVendida() {
        return quantidadeVendida;
    }

    public void setQuantidadeVendida(Long quantidadeVendida) {
        this.quantidadeVendida = quantidadeVendida;
    }
}
