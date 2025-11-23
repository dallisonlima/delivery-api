package com.delivery_api.Projeto.Delivery.API.service;

import com.delivery_api.Projeto.Delivery.API.dto.request.ItemPedidoRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.response.ItemPedidoResponseDTO;
import com.delivery_api.Projeto.Delivery.API.dto.request.PedidoRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.response.PedidoResponseDTO;
import com.delivery_api.Projeto.Delivery.API.entity.*;
import com.delivery_api.Projeto.Delivery.API.repository.*;
import com.delivery_api.Projeto.Delivery.API.exception.EntityNotFoundException;
import com.delivery_api.Projeto.Delivery.API.exception.BusinessException;
import com.delivery_api.Projeto.Delivery.API.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

            if (produto.getQuantidadeEstoque() < itemDTO.getQuantidade()) {
                throw new BusinessException("Estoque insuficiente para o produto: " + produto.getNome());
            }

            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - itemDTO.getQuantidade());
            produtoRepository.save(produto);

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

    @Transactional(readOnly = true)
    public Page<PedidoResponseDTO> listar(StatusPedido status, LocalDate data, Pageable pageable) {
        Specification<Pedido> spec = Specification.where(null);
        if (status != null) {
            spec = spec.and(PedidoSpecs.comStatus(status));
        }
        if (data != null) {
            spec = spec.and(PedidoSpecs.comData(data));
        }
        return pedidoRepository.findAll(spec, pageable).map(this::toPedidoResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponseDTO> listarMeusPedidos(Pageable pageable) {
        Long clienteId = SecurityUtils.getCurrentUserId();
        return buscarPedidosPorCliente(clienteId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponseDTO> listarPedidosDoRestaurante(Pageable pageable) {
        Usuario currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null || currentUser.getRestauranteId() == null) {
            return Page.empty();
        }
        return buscarPedidosPorRestaurante(currentUser.getRestauranteId(), pageable);
    }

    @Transactional(readOnly = true)
    public PedidoResponseDTO buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .map(this::toPedidoResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponseDTO> buscarPedidosPorCliente(Long clienteId, Pageable pageable) {
        Specification<Pedido> spec = PedidoSpecs.doCliente(clienteId);
        return pedidoRepository.findAll(spec, pageable).map(this::toPedidoResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponseDTO> buscarPedidosPorRestaurante(Long restauranteId, Pageable pageable) {
        Specification<Pedido> spec = PedidoSpecs.doRestaurante(restauranteId);
        return pedidoRepository.findAll(spec, pageable).map(this::toPedidoResponseDTO);
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

    public boolean canAccess(Long pedidoId) {
        Usuario currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            return false;
        }

        return pedidoRepository.findById(pedidoId).map(pedido -> {
            boolean isClientOwner = Objects.equals(pedido.getCliente().getId(), currentUser.getId());
            boolean isRestaurantOwner = Objects.equals(pedido.getRestaurante().getId(), currentUser.getRestauranteId());
            return isClientOwner || isRestaurantOwner;
        }).orElse(false);
    }

    public boolean isValidTransition(StatusPedido currentStatus, StatusPedido newStatus) {
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
                return false;
            default:
                return false;
        }
    }

    public PedidoResponseDTO toPedidoResponseDTO(Pedido pedido) {
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

    public ItemPedidoResponseDTO toItemPedidoResponseDTO(ItemPedido itemPedido) {
        ItemPedidoResponseDTO dto = new ItemPedidoResponseDTO();
        dto.setProdutoId(itemPedido.getProduto().getId());
        dto.setProdutoNome(itemPedido.getProduto().getNome());
        dto.setQuantidade(itemPedido.getQuantidade());
        dto.setPrecoUnitario(itemPedido.getProduto().getPreco());
        dto.setSubtotal(itemPedido.getProduto().getPreco().multiply(new BigDecimal(itemPedido.getQuantidade())));
        return dto;
    }
}
