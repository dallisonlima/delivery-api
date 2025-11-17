package com.delivery_api.Projeto.Delivery.API.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PedidoRequestDTO {

    @NotNull(message = "Cliente é obrigatório")
    @Valid
    private IdRequestDTO cliente;

    @NotNull(message = "Restaurante é obrigatório")
    @Valid
    private IdRequestDTO restaurante;

    @NotEmpty(message = "O pedido deve conter pelo menos um item")
    @Valid
    private List<ItemPedidoRequestDTO> itens;

    @NotBlank(message = "Endereço de entrega é obrigatório")
    private String enderecoEntrega;
}
