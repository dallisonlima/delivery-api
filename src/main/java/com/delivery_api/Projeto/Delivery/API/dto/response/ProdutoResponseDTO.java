package com.delivery_api.Projeto.Delivery.API.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(name = "ProdutoResponseDTO", description = "DTO para exibir um produto")
public class ProdutoResponseDTO {

    @Schema(description = "ID do produto", example = "1")
    private Long id;

    @Schema(description = "Nome do produto", example = "Pizza de Calabresa")
    private String nome;

    @Schema(description = "Descrição do produto", example = "Pizza de calabresa com queijo e orégano")
    private String descricao;

    @Schema(description = "Preço do produto", example = "50.00")
    private BigDecimal preco;

    @Schema(description = "Categoria do produto", example = "Pizzas")
    private String categoria;

    @Schema(description = "Indica se o produto está disponível", example = "true")
    private Boolean disponivel;

    @Schema(description = "Quantidade do produto em estoque", example = "100")
    private Integer quantidadeEstoque;

    @Schema(description = "ID do restaurante ao qual o produto pertence", example = "1")
    private Long restauranteId;

    @Schema(description = "Nome do restaurante ao qual o produto pertence", example = "Restaurante do Zé")
    private String nomeRestaurante;
}
