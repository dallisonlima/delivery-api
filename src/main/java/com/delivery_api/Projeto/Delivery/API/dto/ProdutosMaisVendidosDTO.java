package com.delivery_api.Projeto.Delivery.API.dto;

public class ProdutosMaisVendidosDTO {
    private String nomeProduto;
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
