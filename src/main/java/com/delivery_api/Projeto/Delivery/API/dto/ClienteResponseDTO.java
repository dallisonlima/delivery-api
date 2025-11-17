package com.delivery_api.Projeto.Delivery.API.dto;

import lombok.Data;

@Data
public class ClienteResponseDTO {
    private Long id;

    private String nome;

    private String email;

    private String telefone;

    private String endereco;

    private Boolean ativo;
}
