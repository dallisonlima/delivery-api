package com.delivery_api.Projeto.Delivery.API.service;

import com.delivery_api.Projeto.Delivery.API.dto.ItemPedidoRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.ItemPedidoResponseDTO;
import com.delivery_api.Projeto.Delivery.API.dto.PedidoRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.PedidoResponseDTO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    public PedidoResponseDTO criar(PedidoRequestDTO pedidoDTO) {
        Cliente cliente = clienteRepository.findById(pedidoDTO.getCliente().getId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));
        Restaurante restaurante = restauranteRepository.findById(pedidoDTO.getRestaurante().getId())
                .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado."));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setEnderecoEntrega(pedidoDTO.getEnderecoEntrega());

        BigDecimal valorTotalItens = BigDecimal.ZERO;
        StringBuilder descricaoItens = new StringBuilder();
        List<ItemPedido> itensDoPedido = new ArrayList<>();

        for (ItemPedidoRequestDTO itemDTO : pedidoDTO.getItens()) {
            Produto produto = produtoRepository.findById(itemDTO.getProduto().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + itemDTO.getProduto().getId()));

            if (!produto.getDisponivel()) {
                throw new IllegalArgumentException("Produto indisponível: " + produto.getNome());
            }

            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setPedido(pedido);
            itemPedido.setProduto(produto);
            itemPedido.setQuantidade(itemDTO.getQuantidade());
            itemPedido.setPrecoUnitario(produto.getPreco());
            itensDoPedido.add(itemPedido);

            valorTotalItens = valorTotalItens.add(produto.getPreco().multiply(new BigDecimal(itemDTO.getQuantidade())));

            if (descricaoItens.length() > 0) {
                descricaoItens.append(", ");
            }
            descricaoItens.append(produto.getNome()).append(" (").append(itemDTO.getQuantidade()).append("x)");
        }

        pedido.setItens(itensDoPedido);
        pedido.setItensDescricao(descricaoItens.toString());
        pedido.setValorTotal(valorTotalItens.add(restaurante.getTaxaEntrega()));

        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        return toPedidoResponseDTO(pedidoSalvo);
    }

    public PedidoResponseDTO alterarStatus(Long pedidoId, StatusPedido novoStatus) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));
        pedido.setStatus(novoStatus);
        Pedido pedidoAtualizado = pedidoRepository.save(pedido);
        return toPedidoResponseDTO(pedidoAtualizado);
    }

    public PedidoResponseDTO alterarStatusParaCliente(Long clienteId, Long pedidoId, StatusPedido novoStatus) {
        Pedido pedido = pedidoRepository.findByIdAndClienteIdWithItens(pedidoId, clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado para este cliente."));

        pedido.setStatus(novoStatus);
        Pedido pedidoAtualizado = pedidoRepository.save(pedido);
        return toPedidoResponseDTO(pedidoAtualizado);
    }

    public void deletar(Long id) {
        if (!pedidoRepository.existsById(id)) {
            throw new IllegalArgumentException("Pedido não encontrado: " + id);
        }
        pedidoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<PedidoResponseDTO> buscarPorId(Long id) {
        return pedidoRepository.findById(id).map(this::toPedidoResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> buscarPedidosPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteIdWithItens(clienteId).stream()
                .map(this::toPedidoResponseDTO)
                .collect(Collectors.toList());
    }

    private PedidoResponseDTO toPedidoResponseDTO(Pedido pedido) {
        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setId(pedido.getId());
        dto.setNumeroPedido(pedido.getNumeroPedido());
        dto.setClienteId(pedido.getCliente().getId());
        dto.setClienteNome(pedido.getCliente().getNome());
        dto.setRestauranteId(pedido.getRestaurante().getId());
        dto.setRestauranteNome(pedido.getRestaurante().getNome());
        dto.setItens(pedido.getItens().stream()
                .map(this::toItemPedidoResponseDTO)
                .collect(Collectors.toList()));
        dto.setValorTotal(pedido.getValorTotal());
        dto.setStatus(pedido.getStatus());
        dto.setDataPedido(pedido.getDataPedido());
        dto.setEnderecoEntrega(pedido.getEnderecoEntrega());
        return dto;
    }

    private ItemPedidoResponseDTO toItemPedidoResponseDTO(ItemPedido itemPedido) {
        ItemPedidoResponseDTO dto = new ItemPedidoResponseDTO();
        dto.setProdutoId(itemPedido.getProduto().getId());
        dto.setProdutoNome(itemPedido.getProduto().getNome());
        dto.setQuantidade(itemPedido.getQuantidade());
        dto.setPrecoUnitario(itemPedido.getPrecoUnitario());
        dto.setSubtotal(itemPedido.getPrecoUnitario().multiply(new BigDecimal(itemPedido.getQuantidade())));
        return dto;
    }
}
