package com.delivery_api.Projeto.Delivery.API.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@Schema(name = "IdRequestDTO", description = "DTO para representar o ID de uma entidade")
public class IdRequestDTO {
    @NotNull(message = "ID é obrigatório")
    @Positive(message = "ID deve ser um valor positivo")
    @Schema(description = "ID da entidade", example = "1")
    private Long id;
}
