package com.delivery_api.Projeto.Delivery.API.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(name = "PedidoRequestDTO", description = "DTO para criar um novo pedido")
public class PedidoRequestDTO {

    @NotNull(message = "Cliente é obrigatório")
    @Valid
    @Schema(description = "ID do cliente")
    private IdRequestDTO cliente;

    @NotNull(message = "Restaurante é obrigatório")
    @Valid
    @Schema(description = "ID do restaurante")
    private IdRequestDTO restaurante;

    @NotEmpty(message = "O pedido deve conter pelo menos um item")
    @Valid
    @Schema(description = "Lista de itens do pedido")
    private List<ItemPedidoRequestDTO> itens;

    @NotBlank(message = "Endereço de entrega é obrigatório")
    @Schema(description = "Endereço de entrega do pedido", example = "Rua das Flores, 123, São Paulo, SP")
    private String enderecoEntrega;
}
