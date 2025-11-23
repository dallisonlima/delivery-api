package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.request.ProdutoRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.response.ProdutoResponseDTO;
import com.delivery_api.Projeto.Delivery.API.dto.response.ApiResponseWrapper;
import com.delivery_api.Projeto.Delivery.API.dto.response.PagedResponseWrapper;
import com.delivery_api.Projeto.Delivery.API.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/produtos")
@Tag(name = "Produtos", description = "Operações relacionadas a produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @PostMapping
    @PreAuthorize("hasRole('RESTAURANTE') or hasRole('ADMIN')")
    @Operation(summary = "Cadastra um novo produto", description = "Cadastra um novo produto associado a um restaurante.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produto cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<ProdutoResponseDTO>> cadastrar(@Validated @RequestBody ProdutoRequestDTO produtoDTO) {
        ProdutoResponseDTO novoProduto = produtoService.cadastrar(produtoDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novoProduto.getId())
                .toUri();

        return ResponseEntity.created(location).body(ApiResponseWrapper.success(novoProduto, "Produto cadastrado com sucesso."));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @produtoService.isOwner(#id)")
    @Operation(summary = "Atualiza um produto", description = "Atualiza um produto específico com base nas informações fornecidas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "404", description = "Produto ou Restaurante não encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<ProdutoResponseDTO>> atualizar(@Parameter(description = "ID do produto") @PathVariable Long id, @Validated @RequestBody ProdutoRequestDTO produtoDTO) {
        ProdutoResponseDTO produtoAtualizado = produtoService.atualizar(id, produtoDTO);
        return ResponseEntity.ok(ApiResponseWrapper.success(produtoAtualizado, "Produto atualizado com sucesso."));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um produto por ID", description = "Busca um produto específico pelo seu ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<ProdutoResponseDTO>> buscarPorId(@Parameter(description = "ID do produto") @PathVariable Long id) {
        ProdutoResponseDTO produto = produtoService.buscarPorId(id);
        return ResponseEntity.ok(ApiResponseWrapper.success(produto, "Produto encontrado com sucesso."));
    }

    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Busca produtos por categoria", description = "Busca todos os produtos de uma categoria específica de forma paginada.")
    @ApiResponse(responseCode = "200", description = "Produtos encontrados com sucesso")
    public ResponseEntity<PagedResponseWrapper<ProdutoResponseDTO>> buscarProdutosPorCategoria(
            @Parameter(description = "Nome da categoria") @PathVariable String categoria,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<ProdutoResponseDTO> produtos = produtoService.buscarProdutosPorCategoria(categoria, pageable);
        return ResponseEntity.ok(new PagedResponseWrapper<>(produtos));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @produtoService.isOwner(#id)")
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
    @PreAuthorize("hasRole('ADMIN') or @produtoService.isOwner(#id)")
    @Operation(summary = "Altera a disponibilidade de um produto", description = "Ativa ou desativa a disponibilidade de um produto específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Disponibilidade do produto alterada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<ProdutoResponseDTO>> alterarDisponibilidade(@Parameter(description = "ID do produto") @PathVariable Long id) {
        ProdutoResponseDTO produtoAtualizado = produtoService.alterarDisponibilidade(id);
        return ResponseEntity.ok(ApiResponseWrapper.success(produtoAtualizado, "Disponibilidade do produto alterada com sucesso."));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Busca produtos por nome", description = "Busca todos os produtos que contenham o nome informado de forma paginada.")
    @ApiResponse(responseCode = "200", description = "Produtos encontrados com sucesso")
    public ResponseEntity<PagedResponseWrapper<ProdutoResponseDTO>> buscarPorNome(
            @Parameter(description = "Nome do produto") @RequestParam String nome,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<ProdutoResponseDTO> produtos = produtoService.buscarPorNome(nome, pageable);
        return ResponseEntity.ok(new PagedResponseWrapper<>(produtos));
    }
}
