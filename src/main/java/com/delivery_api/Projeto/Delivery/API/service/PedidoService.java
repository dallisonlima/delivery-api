package com.delivery_api.Projeto.Delivery.API.service;

import com.delivery_api.Projeto.Delivery.API.entity.*;
import com.delivery_api.Projeto.Delivery.API.repository.ClienteRepository;
import com.delivery_api.Projeto.Delivery.API.repository.PedidoRepository;
import com.delivery_api.Projeto.Delivery.API.repository.ProdutoRepository;
import com.delivery_api.Projeto.Delivery.API.repository.RestauranteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private RestauranteRepository restauranteRepository;
    @Autowired
    private ProdutoRepository produtoRepository;

    public Pedido criar(Pedido pedido) {
        validarPedido(pedido);

        Cliente cliente = clienteRepository.findById(pedido.getCliente().getId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));
        Restaurante restaurante = restauranteRepository.findById(pedido.getRestaurante().getId())
                .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado."));

        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(StatusPedido.PENDENTE);

        BigDecimal valorTotal = calcularValorTotal(pedido.getItens());
        pedido.setValorTotal(valorTotal.add(restaurante.getTaxaEntrega()));

        return pedidoRepository.save(pedido);
    }

    public Pedido alterarStatus(Long pedidoId, StatusPedido novoStatus) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + pedidoId));

        // Adicionar lógica de transição de status se necessário
        pedido.setStatus(novoStatus);
        return pedidoRepository.save(pedido);
    }

    private void validarPedido(Pedido pedido) {
        if (pedido.getCliente() == null || pedido.getCliente().getId() == null) {
            throw new IllegalArgumentException("O cliente é obrigatório.");
        }
        if (pedido.getRestaurante() == null || pedido.getRestaurante().getId() == null) {
            throw new IllegalArgumentException("O restaurante é obrigatório.");
        }
        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            throw new IllegalArgumentException("O pedido deve conter pelo menos um item.");
        }
    }

    private BigDecimal calcularValorTotal(List<ItemPedido> itens) {
        return itens.stream()
                .map(item -> {
                    Produto produto = produtoRepository.findById(item.getProduto().getId())
                            .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + item.getProduto().getId()));
                    if (!produto.getDisponivel()) {
                        throw new IllegalArgumentException("Produto indisponível: " + produto.getNome());
                    }
                    item.setPrecoUnitario(produto.getPreco());
                    return produto.getPreco().multiply(new BigDecimal(item.getQuantidade()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
