package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.ClientesMaisAtivosDTO;
import com.delivery_api.Projeto.Delivery.API.dto.PedidoResponseDTO;
import com.delivery_api.Projeto.Delivery.API.dto.ProdutosMaisVendidosDTO;
import com.delivery_api.Projeto.Delivery.API.dto.VendasPorRestauranteDTO;
import com.delivery_api.Projeto.Delivery.API.service.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/relatorios")
@CrossOrigin(origins = "*")
@Tag(name = "Relatórios", description = "Operações relacionadas a relatórios")
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    @GetMapping("/vendas-por-restaurante")
    @Operation(summary = "Relatório de vendas por restaurante", description = "Retorna o total de vendas por restaurante.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso")
    })
    public ResponseEntity<List<VendasPorRestauranteDTO>> getVendasPorRestaurante() {
        return ResponseEntity.ok(relatorioService.findTotalVendasPorRestaurante());
    }

    @GetMapping("/produtos-mais-vendidos")
    @Operation(summary = "Relatório de produtos mais vendidos", description = "Retorna os produtos mais vendidos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso")
    })
    public ResponseEntity<List<ProdutosMaisVendidosDTO>> getProdutosMaisVendidos() {
        return ResponseEntity.ok(relatorioService.findProdutosMaisVendidos());
    }

    @GetMapping("/clientes-ativos")
    @Operation(summary = "Relatório de clientes mais ativos", description = "Retorna os clientes mais ativos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso")
    })
    public ResponseEntity<List<ClientesMaisAtivosDTO>> getClientesMaisAtivos() {
        return ResponseEntity.ok(relatorioService.findClientesMaisAtivos());
    }

    @GetMapping("/pedidos-por-periodo")
    @Operation(summary = "Relatório de pedidos por período", description = "Retorna os pedidos realizados em um determinado período.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso")
    })
    public ResponseEntity<List<PedidoResponseDTO>> getPedidosPorPeriodo(
            @Parameter(description = "Data de início do período (formato: yyyy-MM-dd)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @Parameter(description = "Data de fim do período (formato: yyyy-MM-dd)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return ResponseEntity.ok(relatorioService.findPedidosPorPeriodo(dataInicio, dataFim));
    }
}
