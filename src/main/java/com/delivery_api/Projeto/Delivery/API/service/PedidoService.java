package com.delivery_api.Projeto.Delivery.API.service;

import com.delivery_api.Projeto.Delivery.API.dto.ItemPedidoRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.ItemPedidoResponseDTO;
import com.delivery_api.Projeto.Delivery.API.dto.PedidoRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.PedidoResponseDTO;
import com.delivery_api.Projeto.Delivery.API.entity.*;
import com.delivery_api.Projeto.Delivery.API.repository.*;
import com.delivery_api.Projeto.Delivery.API.exception.EntityNotFoundException;
import com.delivery_api.Projeto.Delivery.API.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
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
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado."));
        
        if (!cliente.getAtivo()) {
            throw new BusinessException("Cliente inativo não pode fazer pedidos.");
        }

        Restaurante restaurante = restauranteRepository.findById(pedidoDTO.getRestaurante().getId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado."));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setEnderecoEntrega(pedidoDTO.getEnderecoEntrega());

        List<ItemPedido> itensDoPedido = new ArrayList<>();
        for (ItemPedidoRequestDTO itemDTO : pedidoDTO.getItens()) {
            Produto produto = produtoRepository.findById(itemDTO.getProduto().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + itemDTO.getProduto().getId()));

            if (!produto.getRestaurante().getId().equals(restaurante.getId())) {
                throw new BusinessException("O produto '" + produto.getNome() + "' não pertence ao restaurante selecionado.");
            }

            if (!produto.getDisponivel()) {
                throw new BusinessException("Produto indisponível: " + produto.getNome());
            }

            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setPedido(pedido);
            itemPedido.setProduto(produto);
            itemPedido.setQuantidade(itemDTO.getQuantidade());
            itemPedido.setPrecoUnitario(produto.getPreco());
            itensDoPedido.add(itemPedido);
        }

        pedido.setItens(itensDoPedido);
        pedido.setValorTotal(calcularTotalPedido(pedidoDTO.getRestaurante().getId(), pedidoDTO.getItens()));

        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        return toPedidoResponseDTO(pedidoSalvo);
    }

    public PedidoResponseDTO alterarStatus(Long pedidoId, StatusPedido novoStatus) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado."));

        if (!isValidTransition(pedido.getStatus(), novoStatus)) {
            throw new BusinessException("Transição de status inválida de " + pedido.getStatus() + " para " + novoStatus);
        }

        pedido.setStatus(novoStatus);
        Pedido pedidoAtualizado = pedidoRepository.save(pedido);
        return toPedidoResponseDTO(pedidoAtualizado);
    }

    public PedidoResponseDTO cancelarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado: " + id));

        if (pedido.getStatus() == StatusPedido.ENTREGUE || pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new BusinessException("Não é possível cancelar um pedido com status " + pedido.getStatus());
        }

        pedido.setStatus(StatusPedido.CANCELADO);
        Pedido pedidoCancelado = pedidoRepository.save(pedido);
        return toPedidoResponseDTO(pedidoCancelado);
    }

    public void deletar(Long id) {
        cancelarPedido(id);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listar(StatusPedido status, LocalDate data) {
        Specification<Pedido> spec = Specification.where(null);
        if (status != null) {
            spec = spec.and(PedidoSpecs.comStatus(status));
        }
        if (data != null) {
            spec = spec.and(PedidoSpecs.comData(data));
        }
        return pedidoRepository.findAll(spec).stream()
                .map(this::toPedidoResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<PedidoResponseDTO> buscarPorId(Long id) {
        return pedidoRepository.findById(id).map(this::toPedidoResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> buscarPedidosPorCliente(Long clienteId) {
        Specification<Pedido> spec = PedidoSpecs.doCliente(clienteId);
        return pedidoRepository.findAll(spec).stream()
                .map(this::toPedidoResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> buscarPedidosPorRestaurante(Long restauranteId) {
        Specification<Pedido> spec = PedidoSpecs.doRestaurante(restauranteId);
        return pedidoRepository.findAll(spec).stream()
                .map(this::toPedidoResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularTotalPedido(Long restauranteId, List<ItemPedidoRequestDTO> itensDTO) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado: " + restauranteId));

        BigDecimal total = BigDecimal.ZERO;
        for (ItemPedidoRequestDTO itemDTO : itensDTO) {
            Produto produto = produtoRepository.findById(itemDTO.getProduto().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + itemDTO.getProduto().getId()));

            if (!produto.getRestaurante().getId().equals(restaurante.getId())) {
                throw new BusinessException("O produto '" + produto.getNome() + "' não pertence ao restaurante selecionado.");
            }

            if (!produto.getDisponivel()) {
                throw new BusinessException("Produto indisponível: " + produto.getNome());
            }
            total = total.add(produto.getPreco().multiply(new BigDecimal(itemDTO.getQuantidade())));
        }
        return total.add(restaurante.getTaxaEntrega());
    }

    private boolean isValidTransition(StatusPedido currentStatus, StatusPedido newStatus) {
        // ... (lógica de transição)
        return true;
    }

    private PedidoResponseDTO toPedidoResponseDTO(Pedido pedido) {
        // ... (conversão para DTO)
        return new PedidoResponseDTO();
    }

    private ItemPedidoResponseDTO toItemPedidoResponseDTO(ItemPedido itemPedido) {
        // ... (conversão para DTO)
        return new ItemPedidoResponseDTO();
    }
}
