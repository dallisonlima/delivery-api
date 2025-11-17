package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.ProdutoRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.ProdutoResponseDTO;
import com.delivery_api.Projeto.Delivery.API.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException; // Importar a exceção
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
    @Operation(summary = "Cadastra um novo produto", description = "Cadastra um novo produto com base nas informações fornecidas.")
    @ApiResponse(responseCode = "201", description = "Produto cadastrado com sucesso")
    public ResponseEntity<ProdutoResponseDTO> cadastrar(@Validated @RequestBody ProdutoRequestDTO produtoDTO) {
        ProdutoResponseDTO novoProduto = produtoService.cadastrar(produtoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoProduto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um produto", description = "Atualiza um produto específico com base nas informações fornecidas.")
    @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso")
    public ResponseEntity<ProdutoResponseDTO> atualizar(@Parameter(description = "ID do produto") @PathVariable Long id, @Validated @RequestBody ProdutoRequestDTO produtoDTO) {
        ProdutoResponseDTO produtoAtualizado = produtoService.atualizar(id, produtoDTO);
        return ResponseEntity.ok(produtoAtualizado);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um produto por ID", description = "Busca um produto específico pelo seu ID.")
    @ApiResponse(responseCode = "200", description = "Produto encontrado com sucesso")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@Parameter(description = "ID do produto") @PathVariable Long id) {
        return produtoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Busca produtos por categoria", description = "Busca todos os produtos de uma categoria específica.")
    @ApiResponse(responseCode = "200", description = "Produtos encontrados com sucesso")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarProdutosPorCategoria(@Parameter(description = "Nome da categoria") @PathVariable String categoria) {
        return ResponseEntity.ok(produtoService.buscarProdutosPorCategoria(categoria));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta um produto", description = "Deleta um produto específico.")
    @ApiResponse(responseCode = "204", description = "Produto deletado com sucesso")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    @ApiResponse(responseCode = "409", description = "Não é possível deletar o produto pois ele está associado a pedidos")
    public ResponseEntity<String> deletar(@Parameter(description = "ID do produto") @PathVariable Long id) {
        try {
            produtoService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Não é possível deletar o produto pois ele está associado a pedidos. Considere alterar sua disponibilidade.");
        }
    }

    @PatchMapping("/{id}/disponibilidade")
    @Operation(summary = "Altera a disponibilidade de um produto", description = "Altera a disponibilidade de um produto específico.")
    @ApiResponse(responseCode = "204", description = "Disponibilidade do produto alterada com sucesso")
    public ResponseEntity<Void> alterarDisponibilidade(@Parameter(description = "ID do produto") @PathVariable Long id) {
        produtoService.alterarDisponibilidade(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    @Operation(summary = "Busca produtos por nome", description = "Busca todos os produtos que contenham o nome informado.")
    @ApiResponse(responseCode = "200", description = "Produtos encontrados com sucesso")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorNome(@Parameter(description = "Nome do produto") @RequestParam String nome) {
        return ResponseEntity.ok(produtoService.buscarPorNome(nome));
    }
}
