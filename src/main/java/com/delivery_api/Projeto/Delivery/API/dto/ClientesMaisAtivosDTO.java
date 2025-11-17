package com.delivery_api.Projeto.Delivery.API.dto;

public class ClientesMaisAtivosDTO {
    private String nomeCliente;
    private Long totalPedidos;

    public ClientesMaisAtivosDTO(String nomeCliente, Long totalPedidos) {
        this.nomeCliente = nomeCliente;
        this.totalPedidos = totalPedidos;
    }

    // Getters and Setters
    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public Long getTotalPedidos() {
        return totalPedidos;
    }

    public void setTotalPedidos(Long totalPedidos) {
        this.totalPedidos = totalPedidos;
    }
}
