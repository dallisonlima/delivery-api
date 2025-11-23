package com.delivery_api.Projeto.Delivery.API.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "EnderecoDTO", description = "DTO para exibir um endereço")
public class EnderecoDTO {
    @Schema(description = "CEP do endereço", example = "12345-678")
    private String cep;
    @Schema(description = "Logradouro do endereço", example = "Rua das Flores")
    private String logradouro;
    @Schema(description = "Número do endereço", example = "123")
    private String numero;
    @Schema(description = "Complemento do endereço", example = "Apto 101")
    private String complemento;
    @Schema(description = "Bairro do endereço", example = "Centro")
    private String bairro;
    @Schema(description = "Cidade do endereço", example = "São Paulo")
    private String cidade;
    @Schema(description = "UF do estado", example = "SP")
    private String estado;
}
