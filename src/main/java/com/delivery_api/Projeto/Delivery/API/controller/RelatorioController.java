package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.ClientesMaisAtivosDTO;
import com.delivery_api.Projeto.Delivery.API.dto.PedidoResponseDTO;
import com.delivery_api.Projeto.Delivery.API.dto.ProdutosMaisVendidosDTO;
import com.delivery_api.Projeto.Delivery.API.dto.VendasPorRestauranteDTO;
import com.delivery_api.Projeto.Delivery.API.service.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/relatorios")
@CrossOrigin(origins = "*")
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    @GetMapping("/vendas-por-restaurante")
    public ResponseEntity<List<VendasPorRestauranteDTO>> getVendasPorRestaurante() {
        return ResponseEntity.ok(relatorioService.findTotalVendasPorRestaurante());
    }

    @GetMapping("/produtos-mais-vendidos")
    public ResponseEntity<List<ProdutosMaisVendidosDTO>> getProdutosMaisVendidos() {
        return ResponseEntity.ok(relatorioService.findProdutosMaisVendidos());
    }

    @GetMapping("/clientes-ativos")
    public ResponseEntity<List<ClientesMaisAtivosDTO>> getClientesMaisAtivos() {
        return ResponseEntity.ok(relatorioService.findClientesMaisAtivos());
    }

    @GetMapping("/pedidos-por-periodo")
    public ResponseEntity<List<PedidoResponseDTO>> getPedidosPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return ResponseEntity.ok(relatorioService.findPedidosPorPeriodo(dataInicio, dataFim));
    }
}
