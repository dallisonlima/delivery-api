package com.delivery_api.Projeto.Delivery.API.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "VendasPorRestauranteDTO", description = "DTO para exibir o total de vendas por restaurante")
public class VendasPorRestauranteDTO {

    @Schema(description = "Nome do restaurante", example = "Restaurante do Zé")
    private String nomeRestaurante;

    @Schema(description = "Total de vendas do restaurante", example = "5000.00")
    private BigDecimal totalVendas;

    public VendasPorRestauranteDTO(String nomeRestaurante, BigDecimal totalVendas) {
        this.nomeRestaurante = nomeRestaurante;
        this.totalVendas = totalVendas;
    }

    // Getters and Setters
    public String getNomeRestaurante() {
        return nomeRestaurante;
    }

    public void setNomeRestaurante(String nomeRestaurante) {
        this.nomeRestaurante = nomeRestaurante;
    }

    public BigDecimal getTotalVendas() {
        return totalVendas;
    }

    public void setTotalVendas(BigDecimal totalVendas) {
        this.totalVendas = totalVendas;
    }
}
