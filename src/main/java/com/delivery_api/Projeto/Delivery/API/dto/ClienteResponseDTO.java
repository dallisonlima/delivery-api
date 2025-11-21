package com.delivery_api.Projeto.Delivery.API.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "ClienteResponseDTO", description = "DTO para exibir um cliente")
public class ClienteResponseDTO {

    @Schema(description = "ID do cliente", example = "1")
    private Long id;

    @Schema(description = "Nome do cliente", example = "João da Silva")
    private String nome;

    @Schema(description = "Email do cliente", example = "joao.silva@example.com")
    private String email;

    @Schema(description = "Telefone do cliente", example = "11987654321")
    private String telefone;

    @Schema(description = "Endereço do cliente")
    private EnderecoDTO endereco;

    @Schema(description = "Indica se o cliente está ativo", example = "true")
    private Boolean ativo;
}
