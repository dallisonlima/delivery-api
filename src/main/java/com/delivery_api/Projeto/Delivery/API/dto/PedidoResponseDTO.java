package com.delivery_api.Projeto.Delivery.API.dto;

import com.delivery_api.Projeto.Delivery.API.entity.StatusPedido;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(name = "PedidoResponseDTO", description = "DTO para exibir um pedido")
public class PedidoResponseDTO {

    @Schema(description = "ID do pedido", example = "1")
    private Long id;

    @Schema(description = "Número do pedido", example = "PED-12345")
    private String numeroPedido;

    @Schema(description = "ID do cliente", example = "1")
    private Long clienteId;

    @Schema(description = "Nome do cliente", example = "João da Silva")
    private String clienteNome;

    @Schema(description = "ID do restaurante", example = "1")
    private Long restauranteId;

    @Schema(description = "Nome do restaurante", example = "Restaurante do Zé")
    private String restauranteNome;

    @Schema(description = "Lista de itens do pedido")
    private List<ItemPedidoResponseDTO> itens;

    @Schema(description = "Valor total do pedido", example = "150.00")
    private BigDecimal valorTotal;

    @Schema(description = "Status do pedido", example = "PENDENTE")
    private StatusPedido status;

    @Schema(description = "Data e hora do pedido", example = "2024-07-22T10:00:00")
    private LocalDateTime dataPedido;

    @Schema(description = "Endereço de entrega do pedido", example = "Rua das Flores, 123, São Paulo, SP")
    private String enderecoEntrega;
}
