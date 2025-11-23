package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.PedidoRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.PedidoResponseDTO;
import com.delivery_api.Projeto.Delivery.API.dto.response.ApiResponseWrapper;
import com.delivery_api.Projeto.Delivery.API.dto.response.PagedResponseWrapper;
import com.delivery_api.Projeto.Delivery.API.entity.StatusPedido;
import com.delivery_api.Projeto.Delivery.API.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "Operações relacionadas a pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Cria um novo pedido", description = "Cria um novo pedido com base nas informações fornecidas.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<PedidoResponseDTO>> criar(@Validated @RequestBody PedidoRequestDTO pedidoDTO) {
        PedidoResponseDTO novoPedido = pedidoService.criar(pedidoDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novoPedido.getId())
                .toUri();

        return ResponseEntity.created(location).body(ApiResponseWrapper.success(novoPedido, "Pedido criado com sucesso."));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lista todos os pedidos de forma paginada", description = "Lista todos os pedidos, com a opção de filtrar por status e data.")
    @ApiResponse(responseCode = "200", description = "Pedidos listados com sucesso")
    public ResponseEntity<PagedResponseWrapper<PedidoResponseDTO>> listar(
            @Parameter(description = "Status do pedido") @RequestParam(required = false) StatusPedido status,
            @Parameter(description = "Data do pedido (formato: yyyy-MM-dd)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<PedidoResponseDTO> pedidos = pedidoService.listar(status, data, pageable);
        return ResponseEntity.ok(new PagedResponseWrapper<>(pedidos));
    }

    @GetMapping("/meus")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Lista os pedidos do cliente logado", description = "Lista todos os pedidos feitos pelo cliente autenticado.")
    @ApiResponse(responseCode = "200", description = "Pedidos listados com sucesso")
    public ResponseEntity<PagedResponseWrapper<PedidoResponseDTO>> listarMeusPedidos(@PageableDefault(size = 10) Pageable pageable) {
        Page<PedidoResponseDTO> pedidos = pedidoService.listarMeusPedidos(pageable);
        return ResponseEntity.ok(new PagedResponseWrapper<>(pedidos));
    }

    @GetMapping("/restaurante")
    @PreAuthorize("hasRole('RESTAURANTE')")
    @Operation(summary = "Lista os pedidos do restaurante do usuário logado", description = "Lista todos os pedidos recebidos pelo restaurante do usuário autenticado.")
    @ApiResponse(responseCode = "200", description = "Pedidos listados com sucesso")
    public ResponseEntity<PagedResponseWrapper<PedidoResponseDTO>> listarPedidosRestaurante(@PageableDefault(size = 10) Pageable pageable) {
        Page<PedidoResponseDTO> pedidos = pedidoService.listarPedidosDoRestaurante(pageable);
        return ResponseEntity.ok(new PagedResponseWrapper<>(pedidos));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @pedidoService.canAccess(#id)")
    @Operation(summary = "Busca um pedido por ID", description = "Busca um pedido específico pelo seu ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<PedidoResponseDTO>> buscarPorId(@Parameter(description = "ID do pedido") @PathVariable Long id) {
        PedidoResponseDTO pedido = pedidoService.buscarPorId(id);
        return ResponseEntity.ok(ApiResponseWrapper.success(pedido, "Pedido encontrado com sucesso."));
    }

    @PatchMapping("/{pedidoId}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANTE')")
    @Operation(summary = "Atualiza o status de um pedido", description = "Atualiza o status de um pedido específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status do pedido atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Transição de status inválida"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<PedidoResponseDTO>> atualizarStatusPedido(
            @Parameter(description = "ID do pedido") @PathVariable Long pedidoId,
            @Parameter(description = "Novo status do pedido") @RequestParam StatusPedido status) {
        PedidoResponseDTO pedidoAtualizado = pedidoService.alterarStatus(pedidoId, status);
        return ResponseEntity.ok(ApiResponseWrapper.success(pedidoAtualizado, "Status do pedido atualizado com sucesso."));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cancela um pedido", description = "Cancela um pedido específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido cancelado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Não é possível cancelar o pedido"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<PedidoResponseDTO>> cancelar(@Parameter(description = "ID do pedido") @PathVariable Long id) {
        PedidoResponseDTO pedidoCancelado = pedidoService.cancelarPedido(id);
        return ResponseEntity.ok(ApiResponseWrapper.success(pedidoCancelado, "Pedido cancelado com sucesso."));
    }

    @PostMapping("/calcular")
    @Operation(summary = "Calcula o total de um pedido", description = "Calcula o total de um pedido com base nos itens e no restaurante.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Total do pedido calculado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<BigDecimal>> calcularTotal(@RequestBody PedidoRequestDTO pedidoDTO) {
        BigDecimal total = pedidoService.calcularTotalPedido(pedidoDTO.getRestaurante().getId(), pedidoDTO.getItens());
        return ResponseEntity.ok(ApiResponseWrapper.success(total, "Total do pedido calculado com sucesso."));
    }
}
