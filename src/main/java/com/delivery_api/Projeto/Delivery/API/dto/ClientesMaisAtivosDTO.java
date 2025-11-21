package com.delivery_api.Projeto.Delivery.API.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ClientesMaisAtivosDTO", description = "DTO para exibir os clientes mais ativos")
public class ClientesMaisAtivosDTO {

    @Schema(description = "Nome do cliente", example = "João da Silva")
    private String nomeCliente;

    @Schema(description = "Total de pedidos do cliente", example = "10")
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
