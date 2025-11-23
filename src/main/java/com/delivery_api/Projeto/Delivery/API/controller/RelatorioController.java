package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.ClientesMaisAtivosDTO;
import com.delivery_api.Projeto.Delivery.API.dto.response.PedidoResponseDTO;
import com.delivery_api.Projeto.Delivery.API.dto.ProdutosMaisVendidosDTO;
import com.delivery_api.Projeto.Delivery.API.dto.VendasPorRestauranteDTO;
import com.delivery_api.Projeto.Delivery.API.dto.response.ApiResponseWrapper;
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
@Tag(name = "Relatórios", description = "Operações relacionadas a relatórios")
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    @GetMapping("/vendas-por-restaurante")
    @Operation(summary = "Relatório de vendas por restaurante", description = "Retorna o total de vendas por restaurante.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso")
    })
    public ResponseEntity<ApiResponseWrapper<List<VendasPorRestauranteDTO>>> getVendasPorRestaurante() {
        List<VendasPorRestauranteDTO> relatorio = relatorioService.findTotalVendasPorRestaurante();
        return ResponseEntity.ok(ApiResponseWrapper.success(relatorio, "Relatório de vendas por restaurante gerado com sucesso."));
    }

    @GetMapping("/produtos-mais-vendidos")
    @Operation(summary = "Relatório de produtos mais vendidos", description = "Retorna os produtos mais vendidos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso")
    })
    public ResponseEntity<ApiResponseWrapper<List<ProdutosMaisVendidosDTO>>> getProdutosMaisVendidos() {
        List<ProdutosMaisVendidosDTO> relatorio = relatorioService.findProdutosMaisVendidos();
        return ResponseEntity.ok(ApiResponseWrapper.success(relatorio, "Relatório de produtos mais vendidos gerado com sucesso."));
    }

    @GetMapping("/clientes-ativos")
    @Operation(summary = "Relatório de clientes mais ativos", description = "Retorna os clientes mais ativos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso")
    })
    public ResponseEntity<ApiResponseWrapper<List<ClientesMaisAtivosDTO>>> getClientesMaisAtivos() {
        List<ClientesMaisAtivosDTO> relatorio = relatorioService.findClientesMaisAtivos();
        return ResponseEntity.ok(ApiResponseWrapper.success(relatorio, "Relatório de clientes mais ativos gerado com sucesso."));
    }

    @GetMapping("/pedidos-por-periodo")
    @Operation(summary = "Relatório de pedidos por período", description = "Retorna os pedidos realizados em um determinado período.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso")
    })
    public ResponseEntity<ApiResponseWrapper<List<PedidoResponseDTO>>> getPedidosPorPeriodo(
            @Parameter(description = "Data de início do período (formato: yyyy-MM-dd)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @Parameter(description = "Data de fim do período (formato: yyyy-MM-dd)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        List<PedidoResponseDTO> relatorio = relatorioService.findPedidosPorPeriodo(dataInicio, dataFim);
        return ResponseEntity.ok(ApiResponseWrapper.success(relatorio, "Relatório de pedidos por período gerado com sucesso."));
    }
}
