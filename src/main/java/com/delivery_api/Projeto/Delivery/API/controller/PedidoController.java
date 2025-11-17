package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.PedidoRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.PedidoResponseDTO;
import com.delivery_api.Projeto.Delivery.API.entity.StatusPedido;
import com.delivery_api.Projeto.Delivery.API.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
@Tag(name = "Pedidos", description = "Operações relacionadas a pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    @Operation(summary = "Cria um novo pedido", description = "Cria um novo pedido com base nas informações fornecidas.")
    @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso")
    public ResponseEntity<PedidoResponseDTO> criar(@Validated @RequestBody PedidoRequestDTO pedidoDTO) {
        PedidoResponseDTO novoPedido = pedidoService.criar(pedidoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoPedido);
    }

    @GetMapping
    @Operation(summary = "Lista todos os pedidos", description = "Lista todos os pedidos, com a opção de filtrar por status e data.")
    @ApiResponse(responseCode = "200", description = "Pedidos listados com sucesso")
    public ResponseEntity<List<PedidoResponseDTO>> listar(
            @Parameter(description = "Status do pedido") @RequestParam(required = false) StatusPedido status,
            @Parameter(description = "Data do pedido (formato: yyyy-MM-dd)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return ResponseEntity.ok(pedidoService.listar(status, data));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um pedido por ID", description = "Busca um pedido específico pelo seu ID.")
    @ApiResponse(responseCode = "200", description = "Pedido encontrado com sucesso")
    @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    public ResponseEntity<PedidoResponseDTO> buscarPorId(@Parameter(description = "ID do pedido") @PathVariable Long id) {
        return pedidoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{pedidoId}/status")
    @Operation(summary = "Atualiza o status de um pedido", description = "Atualiza o status de um pedido específico.")
    @ApiResponse(responseCode = "200", description = "Status do pedido atualizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Requisição inválida")
    public ResponseEntity<PedidoResponseDTO> atualizarStatusPedido(
            @Parameter(description = "ID do pedido") @PathVariable Long pedidoId,
            @Parameter(description = "Novo status do pedido") @RequestParam StatusPedido status) {
        try {
            PedidoResponseDTO pedidoAtualizado = pedidoService.alterarStatus(pedidoId, status);
            return ResponseEntity.ok(pedidoAtualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancela um pedido", description = "Cancela um pedido específico.")
    @ApiResponse(responseCode = "204", description = "Pedido cancelado com sucesso")
    @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    public ResponseEntity<Void> cancelar(@Parameter(description = "ID do pedido") @PathVariable Long id) {
        try {
            pedidoService.cancelarPedido(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/calcular")
    @Operation(summary = "Calcula o total de um pedido", description = "Calcula o total de um pedido com base nos itens e no restaurante.")
    @ApiResponse(responseCode = "200", description = "Total do pedido calculado com sucesso")
    public ResponseEntity<BigDecimal> calcularTotal(@RequestBody PedidoRequestDTO pedidoDTO) {
        BigDecimal total = pedidoService.calcularTotalPedido(pedidoDTO.getRestaurante().getId(), pedidoDTO.getItens());
        return ResponseEntity.ok(total);
    }
}
