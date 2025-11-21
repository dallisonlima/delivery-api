package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.PedidoResponseDTO;
import com.delivery_api.Projeto.Delivery.API.dto.ProdutoResponseDTO;
import com.delivery_api.Projeto.Delivery.API.dto.RestauranteRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.RestauranteResponseDTO;
import com.delivery_api.Projeto.Delivery.API.dto.response.ApiResponseWrapper;
import com.delivery_api.Projeto.Delivery.API.dto.response.PagedResponseWrapper;
import com.delivery_api.Projeto.Delivery.API.service.PedidoService;
import com.delivery_api.Projeto.Delivery.API.service.ProdutoService;
import com.delivery_api.Projeto.Delivery.API.service.RestauranteService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;

@RestController
@RequestMapping("/api/restaurantes")
@Tag(name = "Restaurantes", description = "Operações relacionadas a restaurantes")
public class RestauranteController {

    @Autowired
    private RestauranteService restauranteService;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    @Operation(summary = "Cadastra um novo restaurante", description = "Cadastra um novo restaurante com base nas informações fornecidas.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Restaurante cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    public ResponseEntity<ApiResponseWrapper<RestauranteResponseDTO>> cadastrar(@Validated @RequestBody RestauranteRequestDTO restauranteDTO) {
        RestauranteResponseDTO novoRestaurante = restauranteService.cadastrar(restauranteDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novoRestaurante.getId())
                .toUri();

        return ResponseEntity.created(location).body(ApiResponseWrapper.success(novoRestaurante, "Restaurante cadastrado com sucesso."));
    }

    @GetMapping
    @Operation(summary = "Lista todos os restaurantes de forma paginada", description = "Lista todos os restaurantes, com a opção de filtrar por categoria e status de atividade.")
    @ApiResponse(responseCode = "200", description = "Restaurantes listados com sucesso")
    public ResponseEntity<PagedResponseWrapper<RestauranteResponseDTO>> listar(
            @Parameter(description = "Categoria do restaurante") @RequestParam(required = false) String categoria,
            @Parameter(description = "Status do restaurante (ativo/inativo)") @RequestParam(required = false) Boolean ativo,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<RestauranteResponseDTO> restaurantes = restauranteService.listar(categoria, ativo, pageable);
        return ResponseEntity.ok(new PagedResponseWrapper<>(restaurantes));
    }

    @GetMapping("/disponiveis")
    @Operation(summary = "Busca restaurantes disponíveis de forma paginada", description = "Busca todos os restaurantes que estão ativos.")
    @ApiResponse(responseCode = "200", description = "Restaurantes encontrados com sucesso")
    public ResponseEntity<PagedResponseWrapper<RestauranteResponseDTO>> buscarRestaurantesDisponiveis(@PageableDefault(size = 10) Pageable pageable) {
        Page<RestauranteResponseDTO> restaurantes = restauranteService.buscarRestaurantesDisponiveis(pageable);
        return ResponseEntity.ok(new PagedResponseWrapper<>(restaurantes));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um restaurante por ID", description = "Busca um restaurante específico pelo seu ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Restaurante encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<RestauranteResponseDTO>> buscarPorId(@Parameter(description = "ID do restaurante") @PathVariable Long id) {
        RestauranteResponseDTO restaurante = restauranteService.buscarPorId(id);
        return ResponseEntity.ok(ApiResponseWrapper.success(restaurante, "Restaurante encontrado com sucesso."));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um restaurante", description = "Atualiza um restaurante específico com base nas informações fornecidas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Restaurante atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<RestauranteResponseDTO>> atualizar(@Parameter(description = "ID do restaurante") @PathVariable Long id, @Validated @RequestBody RestauranteRequestDTO restauranteDTO) {
        RestauranteResponseDTO restauranteAtualizado = restauranteService.atualizar(id, restauranteDTO);
        return ResponseEntity.ok(ApiResponseWrapper.success(restauranteAtualizado, "Restaurante atualizado com sucesso."));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta um restaurante", description = "Deleta um restaurante específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Restaurante deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado"),
            @ApiResponse(responseCode = "409", description = "Não é possível deletar o restaurante pois ele possui pedidos associados")
    })
    public ResponseEntity<Void> deletar(@Parameter(description = "ID do restaurante") @PathVariable Long id) {
        restauranteService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Busca restaurantes por categoria de forma paginada", description = "Busca todos os restaurantes de uma categoria específica.")
    @ApiResponse(responseCode = "200", description = "Restaurantes encontrados com sucesso")
    public ResponseEntity<PagedResponseWrapper<RestauranteResponseDTO>> buscarPorCategoria(
            @Parameter(description = "Nome da categoria") @PathVariable String categoria,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<RestauranteResponseDTO> restaurantes = restauranteService.buscarPorCategoria(categoria, pageable);
        return ResponseEntity.ok(new PagedResponseWrapper<>(restaurantes));
    }

    @GetMapping("/{restauranteId}/taxa-entrega")
    @Operation(summary = "Retorna a taxa de entrega", description = "Retorna a taxa de entrega de um restaurante.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Taxa de entrega retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<BigDecimal>> calcularTaxaEntrega(@Parameter(description = "ID do restaurante") @PathVariable Long restauranteId) {
        BigDecimal taxa = restauranteService.calcularTaxaEntrega(restauranteId, null);
        return ResponseEntity.ok(ApiResponseWrapper.success(taxa, "Taxa de entrega retornada com sucesso."));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Ativa ou desativa um restaurante", description = "Ativa ou desativa um restaurante específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status do restaurante alterado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<RestauranteResponseDTO>> ativarOuDesativar(@Parameter(description = "ID do restaurante") @PathVariable Long id, @Parameter(description = "Novo status do restaurante") @RequestParam boolean ativo) {
        RestauranteResponseDTO restauranteAtualizado = restauranteService.ativarOuDesativar(id, ativo);
        return ResponseEntity.ok(ApiResponseWrapper.success(restauranteAtualizado, "Status do restaurante alterado com sucesso."));
    }

    @GetMapping("/{restauranteId}/produtos")
    @Operation(summary = "Busca os produtos de um restaurante de forma paginada", description = "Busca todos os produtos de um restaurante específico.")
    @ApiResponse(responseCode = "200", description = "Produtos do restaurante listados com sucesso")
    public ResponseEntity<PagedResponseWrapper<ProdutoResponseDTO>> buscarProdutosPorRestaurante(
            @Parameter(description = "ID do restaurante") @PathVariable Long restauranteId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<ProdutoResponseDTO> produtos = produtoService.buscarProdutosPorRestaurante(restauranteId, pageable);
        return ResponseEntity.ok(new PagedResponseWrapper<>(produtos));
    }

    @GetMapping("/{restauranteId}/pedidos")
    @Operation(summary = "Busca os pedidos de um restaurante de forma paginada", description = "Busca todos os pedidos de um restaurante específico.")
    @ApiResponse(responseCode = "200", description = "Pedidos do restaurante listados com sucesso")
    public ResponseEntity<PagedResponseWrapper<PedidoResponseDTO>> buscarPedidosPorRestaurante(
            @Parameter(description = "ID do restaurante") @PathVariable Long restauranteId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<PedidoResponseDTO> pedidos = pedidoService.buscarPedidosPorRestaurante(restauranteId, pageable);
        return ResponseEntity.ok(new PagedResponseWrapper<>(pedidos));
    }
}
