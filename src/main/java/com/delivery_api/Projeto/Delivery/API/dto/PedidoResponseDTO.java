package com.delivery_api.Projeto.Delivery.API.dto;

import com.delivery_api.Projeto.Delivery.API.entity.StatusPedido;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PedidoResponseDTO {
    private Long id;
    private String numeroPedido;
    private Long clienteId;
    private String clienteNome;
    private Long restauranteId;
    private String restauranteNome;
    private List<ItemPedidoResponseDTO> itens;
    private BigDecimal valorTotal;
    private StatusPedido status;
    private LocalDateTime dataPedido;
    private String enderecoEntrega;
}
