package com.delivery_api.Projeto.Delivery.API.service;

import com.delivery_api.Projeto.Delivery.API.dto.ClientesMaisAtivosDTO;
import com.delivery_api.Projeto.Delivery.API.dto.response.PedidoResponseDTO;
import com.delivery_api.Projeto.Delivery.API.dto.ProdutosMaisVendidosDTO;
import com.delivery_api.Projeto.Delivery.API.dto.VendasPorRestauranteDTO;
import com.delivery_api.Projeto.Delivery.API.repository.ItemPedidoRepository;
import com.delivery_api.Projeto.Delivery.API.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RelatorioService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    @Autowired
    private PedidoService pedidoService;

    public List<VendasPorRestauranteDTO> findTotalVendasPorRestaurante() {
        return pedidoRepository.findTotalVendasPorRestaurante().stream()
                .map(obj -> new VendasPorRestauranteDTO((String) obj[0], (BigDecimal) obj[1]))
                .collect(Collectors.toList());
    }

    public List<ProdutosMaisVendidosDTO> findProdutosMaisVendidos() {
        return itemPedidoRepository.findProdutosMaisVendidos().stream()
                .map(obj -> new ProdutosMaisVendidosDTO((String) obj[0], (Long) obj[1]))
                .collect(Collectors.toList());
    }

    public List<ClientesMaisAtivosDTO> findClientesMaisAtivos() {
        return pedidoRepository.findClientesMaisAtivos().stream()
                .map(obj -> new ClientesMaisAtivosDTO((String) obj[0], (Long) obj[1]))
                .collect(Collectors.toList());
    }

    public List<PedidoResponseDTO> findPedidosPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim.atTime(23, 59, 59);
        return pedidoRepository.findByDataPedidoBetween(inicio, fim).stream()
                .map(pedido -> pedidoService.toPedidoResponseDTO(pedido))
                .collect(Collectors.toList());
    }
}
