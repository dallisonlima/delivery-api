package com.delivery_api.Projeto.Delivery.API.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@Schema(name = "ItemPedidoRequestDTO", description = "DTO para adicionar um item a um pedido")
public class ItemPedidoRequestDTO {

    @NotNull(message = "Produto é obrigatório")
    @Valid
    @Schema(description = "ID do produto")
    private IdRequestDTO produto;

    @NotNull(message = "Quantidade é obrigatória")
    @Positive(message = "Quantidade deve ser um valor positivo")
    @Schema(description = "Quantidade do produto", example = "2")
    private Integer quantidade;
}
