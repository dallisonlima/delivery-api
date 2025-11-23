package com.delivery_api.Projeto.Delivery.API.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(name = "ItemPedidoResponseDTO", description = "DTO para exibir um item de um pedido")
public class ItemPedidoResponseDTO {

    @Schema(description = "ID do produto", example = "1")
    private Long produtoId;

    @Schema(description = "Nome do produto", example = "Pizza de Calabresa")
    private String produtoNome;

    @Schema(description = "Quantidade do produto", example = "2")
    private Integer quantidade;

    @Schema(description = "Preço unitário do produto", example = "50.00")
    private BigDecimal precoUnitario;

    @Schema(description = "Subtotal do item", example = "100.00")
    private BigDecimal subtotal;
}
