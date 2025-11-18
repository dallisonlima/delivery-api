package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.ProdutoRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.ProdutoResponseDTO;
import com.delivery_api.Projeto.Delivery.API.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "*")
@Tag(name = "Produtos", description = "Operações relacionadas a produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @PostMapping
    @Operation(summary = "Cadastra um novo produto", description = "Cadastra um novo produto associado a um restaurante.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produto cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    public ResponseEntity<ProdutoResponseDTO> cadastrar(@Validated @RequestBody ProdutoRequestDTO produtoDTO) {
        ProdutoResponseDTO novoProduto = produtoService.cadastrar(produtoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoProduto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um produto", description = "Atualiza um produto específico com base nas informações fornecidas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "404", description = "Produto ou Restaurante não encontrado")
    })
    public ResponseEntity<ProdutoResponseDTO> atualizar(@Parameter(description = "ID do produto") @PathVariable Long id, @Validated @RequestBody ProdutoRequestDTO produtoDTO) {
        ProdutoResponseDTO produtoAtualizado = produtoService.atualizar(id, produtoDTO);
        return ResponseEntity.ok(produtoAtualizado);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um produto por ID", description = "Busca um produto específico pelo seu ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@Parameter(description = "ID do produto") @PathVariable Long id) {
        ProdutoResponseDTO produto = produtoService.buscarPorId(id);
        return ResponseEntity.ok(produto);
    }

    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Busca produtos por categoria", description = "Busca todos os produtos de uma categoria específica.")
    @ApiResponse(responseCode = "200", description = "Produtos encontrados com sucesso")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarProdutosPorCategoria(@Parameter(description = "Nome da categoria") @PathVariable String categoria) {
        return ResponseEntity.ok(produtoService.buscarProdutosPorCategoria(categoria));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta um produto", description = "Deleta um produto específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Produto deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
            @ApiResponse(responseCode = "409", description = "Não é possível deletar o produto pois ele está associado a pedidos")
    })
    public ResponseEntity<Void> deletar(@Parameter(description = "ID do produto") @PathVariable Long id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/disponibilidade")
    @Operation(summary = "Altera a disponibilidade de um produto", description = "Ativa ou desativa a disponibilidade de um produto específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Disponibilidade do produto alterada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<ProdutoResponseDTO> alterarDisponibilidade(@Parameter(description = "ID do produto") @PathVariable Long id) {
        ProdutoResponseDTO produtoAtualizado = produtoService.alterarDisponibilidade(id);
        return ResponseEntity.ok(produtoAtualizado);
    }

    @GetMapping("/buscar")
    @Operation(summary = "Busca produtos por nome", description = "Busca todos os produtos que contenham o nome informado.")
    @ApiResponse(responseCode = "200", description = "Produtos encontrados com sucesso")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorNome(@Parameter(description = "Nome do produto") @RequestParam String nome) {
        return ResponseEntity.ok(produtoService.buscarPorNome(nome));
    }
}
