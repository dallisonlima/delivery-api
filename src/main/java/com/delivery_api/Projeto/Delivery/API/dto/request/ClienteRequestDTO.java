package com.delivery_api.Projeto.Delivery.API.dto.request;

import com.delivery_api.Projeto.Delivery.API.validation.ValidCEP;
import com.delivery_api.Projeto.Delivery.API.validation.ValidTelefone;
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
    @ValidTelefone
    @Schema(description = "Telefone do cliente (apenas números)", example = "11987654321")
    private String telefone;

    @NotBlank(message = "CEP é obrigatório")
    @ValidCEP
    @Schema(description = "CEP do cliente", example = "12345-678")
    private String cep;

    @NotBlank(message = "Logradouro é obrigatório")
    @Schema(description = "Logradouro do endereço", example = "Rua das Flores")
    private String logradouro;

    @NotBlank(message = "Número é obrigatório")
    @Schema(description = "Número do endereço", example = "123")
    private String numero;

    @Schema(description = "Complemento do endereço", example = "Apto 101")
    private String complemento;

    @NotBlank(message = "Bairro é obrigatório")
    @Schema(description = "Bairro do endereço", example = "Centro")
    private String bairro;

    @NotBlank(message = "Cidade é obrigatória")
    @Schema(description = "Cidade do endereço", example = "São Paulo")
    private String cidade;

    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres")
    @Schema(description = "UF do estado", example = "SP")
    private String estado;
}
