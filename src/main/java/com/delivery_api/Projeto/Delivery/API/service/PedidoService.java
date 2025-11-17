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
        
        // Validação: Verificar se o cliente está ativo
        if (!cliente.getAtivo()) {
            throw new IllegalArgumentException("Cliente inativo não pode fazer pedidos.");
        }

        Restaurante restaurante = restauranteRepository.findById(pedidoDTO.getRestaurante().getId())
                .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado."));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setEnderecoEntrega(pedidoDTO.getEnderecoEntrega());

        StringBuilder descricaoItens = new StringBuilder();
        List<ItemPedido> itensDoPedido = new ArrayList<>();

        for (ItemPedidoRequestDTO itemDTO : pedidoDTO.getItens()) {
            Produto produto = produtoRepository.findById(itemDTO.getProduto().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + itemDTO.getProduto().getId()));

            // Validação: Verificar se o produto pertence ao restaurante do pedido
            if (!produto.getRestaurante().getId().equals(restaurante.getId())) {
                throw new IllegalArgumentException("O produto '" + produto.getNome() + "' não pertence ao restaurante selecionado.");
            }

            if (!produto.getDisponivel()) {
                throw new IllegalArgumentException("Produto indisponível: " + produto.getNome());
            }

            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setPedido(pedido);
            itemPedido.setProduto(produto);
            itemPedido.setQuantidade(itemDTO.getQuantidade());
            itemPedido.setPrecoUnitario(produto.getPreco());
            itensDoPedido.add(itemPedido);

            if (descricaoItens.length() > 0) {
                descricaoItens.append(", ");
            }
            descricaoItens.append(produto.getNome()).append(" (").append(itemDTO.getQuantidade()).append("x)");
        }

        pedido.setItens(itensDoPedido);
        pedido.setItensDescricao(descricaoItens.toString());
        // Utiliza o método calcularTotalPedido para definir o valor total
        pedido.setValorTotal(calcularTotalPedido(pedidoDTO.getRestaurante().getId(), pedidoDTO.getItens()));

        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        return toPedidoResponseDTO(pedidoSalvo);
    }

    public PedidoResponseDTO alterarStatus(Long pedidoId, StatusPedido novoStatus) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        if (!isValidTransition(pedido.getStatus(), novoStatus)) {
            throw new IllegalArgumentException("Transição de status inválida de " + pedido.getStatus() + " para " + novoStatus);
        }

        pedido.setStatus(novoStatus);
        Pedido pedidoAtualizado = pedidoRepository.save(pedido);
        return toPedidoResponseDTO(pedidoAtualizado);
    }

    public PedidoResponseDTO cancelarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + id));

        // Valida se o status atual permite o cancelamento
        if (pedido.getStatus() == StatusPedido.ENTREGUE || pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new IllegalArgumentException("Não é possível cancelar um pedido com status " + pedido.getStatus());
        }

        pedido.setStatus(StatusPedido.CANCELADO);
        Pedido pedidoCancelado = pedidoRepository.save(pedido);
        return toPedidoResponseDTO(pedidoCancelado);
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

    @Transactional(readOnly = true)
    public BigDecimal calcularTotalPedido(Long restauranteId, List<ItemPedidoRequestDTO> itensDTO) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado: " + restauranteId));

        BigDecimal total = BigDecimal.ZERO;
        for (ItemPedidoRequestDTO itemDTO : itensDTO) {
            Produto produto = produtoRepository.findById(itemDTO.getProduto().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + itemDTO.getProduto().getId()));

            // Validação: Verificar se o produto pertence ao restaurante do pedido
            if (!produto.getRestaurante().getId().equals(restaurante.getId())) {
                throw new IllegalArgumentException("O produto '" + produto.getNome() + "' não pertence ao restaurante selecionado.");
            }

            if (!produto.getDisponivel()) {
                throw new IllegalArgumentException("Produto indisponível: " + produto.getNome());
            }
            total = total.add(produto.getPreco().multiply(new BigDecimal(itemDTO.getQuantidade())));
        }
        return total.add(restaurante.getTaxaEntrega());
    }

    private boolean isValidTransition(StatusPedido currentStatus, StatusPedido newStatus) {
        switch (currentStatus) {
            case PENDENTE:
                return newStatus == StatusPedido.CONFIRMADO || newStatus == StatusPedido.CANCELADO;
            case CONFIRMADO:
                return newStatus == StatusPedido.EM_PREPARO || newStatus == StatusPedido.CANCELADO;
            case EM_PREPARO:
                return newStatus == StatusPedido.SAIU_PARA_ENTREGA;
            case SAIU_PARA_ENTREGA:
                return newStatus == StatusPedido.ENTREGUE;
            case ENTREGUE:
            case CANCELADO:
                return false; // Estados finais, não permitem transições
            default:
                return false;
        }
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
        dto.setPrecoUnitario(itemPedido.getProduto().getPreco());
        dto.setSubtotal(itemPedido.getProduto().getPreco().multiply(new BigDecimal(itemPedido.getQuantidade())));
        return dto;
    }
}
