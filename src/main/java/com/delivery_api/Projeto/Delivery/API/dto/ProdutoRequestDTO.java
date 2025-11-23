package com.delivery_api.Projeto.Delivery.API.dto;

import com.delivery_api.Projeto.Delivery.API.validation.ValidCategoria;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(name = "ProdutoRequestDTO", description = "DTO para criar ou atualizar um produto")
public class ProdutoRequestDTO {

    @NotBlank(message = "Nome do produto é obrigatório")
    @Size(min = 2, max = 50, message = "Nome do produto deve ter entre 2 e 50 caracteres")
    @Schema(description = "Nome do produto", example = "Pizza de Calabresa")
    private String nome;

    @NotBlank(message = "Descrição do produto é obrigatória")
    @Size(min = 10, message = "Descrição deve ter no mínimo 10 caracteres")
    @Schema(description = "Descrição do produto", example = "Deliciosa pizza de calabresa com queijo mussarela e orégano fresco.")
    private String descricao;

    @NotNull(message = "Preço do produto é obrigatório")
    @Positive(message = "Preço do produto deve ser um valor positivo")
    @DecimalMax(value = "500.00", message = "Preço do produto não pode exceder R$ 500,00")
    @Schema(description = "Preço do produto", example = "50.00")
    private BigDecimal preco;

    @NotBlank(message = "Categoria do produto é obrigatória")
    @ValidCategoria
    @Schema(description = "Categoria do produto", example = "Pizza")
    private String categoria;

    @NotNull(message = "Disponibilidade do produto é obrigatória")
    @Schema(description = "Indica se o produto está disponível", example = "true")
    private Boolean disponivel;

    @NotNull(message = "Quantidade em estoque é obrigatória")
    @PositiveOrZero(message = "Quantidade em estoque deve ser um valor positivo ou zero")
    @Schema(description = "Quantidade do produto em estoque", example = "100")
    private Integer quantidadeEstoque;

    @NotNull(message = "ID do restaurante é obrigatório")
    @Schema(description = "ID do restaurante ao qual o produto pertence", example = "1")
    private Long restauranteId;
}
