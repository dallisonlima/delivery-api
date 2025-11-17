package com.delivery_api.Projeto.Delivery.API.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(name = "ClienteRequestDTO", description = "DTO para criar ou atualizar um cliente")
public class ClienteRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, message = "Nome deve ter pelo menos 2 caracteres")
    @Schema(description = "Nome do cliente", example = "João da Silva")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Formato de email inválido")
    @Schema(description = "Email do cliente", example = "joao.silva@example.com")
    private String email;

    @NotBlank(message = "Telefone é obrigatório")
    @Schema(description = "Telefone do cliente", example = "(11) 99999-9999")
    private String telefone;

    @NotBlank(message = "Endereço é obrigatório")
    @Schema(description = "Endereço do cliente", example = "Rua das Flores, 123, São Paulo, SP")
    private String endereco;
}
