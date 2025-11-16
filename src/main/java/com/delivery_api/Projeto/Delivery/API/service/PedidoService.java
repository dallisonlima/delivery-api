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
import java.util.stream.Collectors;

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

        BigDecimal valorTotalItens = BigDecimal.ZERO;
        StringBuilder descricaoItens = new StringBuilder();

        for (ItemPedido item : pedido.getItens()) {
            Produto produto = produtoRepository.findById(item.getProduto().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + item.getProduto().getId()));

            if (!produto.getDisponivel()) {
                throw new IllegalArgumentException("Produto indisponível: " + produto.getNome());
            }

            item.setPedido(pedido);
            item.setPrecoUnitario(produto.getPreco());
            valorTotalItens = valorTotalItens.add(produto.getPreco().multiply(new BigDecimal(item.getQuantidade())));

            if (descricaoItens.length() > 0) {
                descricaoItens.append(", ");
            }
            descricaoItens.append(produto.getNome()).append(" (").append(item.getQuantidade()).append("x)");
        }

        pedido.setItensDescricao(descricaoItens.toString());
        pedido.setValorTotal(valorTotalItens.add(restaurante.getTaxaEntrega()));

        return pedidoRepository.save(pedido);
    }

    public Pedido alterarStatusParaCliente(Long clienteId, Long pedidoId, StatusPedido novoStatus) {
        Pedido pedido = pedidoRepository.findByIdAndClienteIdWithItens(pedidoId, clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado para este cliente."));

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
}
