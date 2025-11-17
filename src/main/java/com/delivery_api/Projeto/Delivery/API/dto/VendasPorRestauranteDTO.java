package com.delivery_api.Projeto.Delivery.API.dto;

import java.math.BigDecimal;

public class VendasPorRestauranteDTO {
    private String nomeRestaurante;
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
