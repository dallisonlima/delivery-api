package com.delivery_api.Projeto.Delivery.API.service;

import com.delivery_api.Projeto.Delivery.API.dto.IdRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.ItemPedidoRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.PedidoRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.PedidoResponseDTO;
import com.delivery_api.Projeto.Delivery.API.entity.*;
import com.delivery_api.Projeto.Delivery.API.exception.BusinessException;
import com.delivery_api.Projeto.Delivery.API.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private RestauranteRepository restauranteRepository;
    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private PedidoService pedidoService;

    private Cliente cliente;
    private Restaurante restaurante;
    private Produto produto;
    private PedidoRequestDTO pedidoRequestDTO;

    private IdRequestDTO createIdRequest(Long id) {
        IdRequestDTO idRequest = new IdRequestDTO();
        idRequest.setId(id);
        return idRequest;
    }

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setAtivo(true);

        restaurante = new Restaurante();
        restaurante.setId(1L);
        restaurante.setTaxaEntrega(BigDecimal.valueOf(5));

        produto = new Produto();
        produto.setId(1L);
        produto.setRestaurante(restaurante);
        produto.setDisponivel(true);
        produto.setPreco(BigDecimal.valueOf(20));
        produto.setQuantidadeEstoque(10);

        ItemPedidoRequestDTO itemDTO = new ItemPedidoRequestDTO();
        itemDTO.setProduto(createIdRequest(1L));
        itemDTO.setQuantidade(2);

        pedidoRequestDTO = new PedidoRequestDTO();
        pedidoRequestDTO.setCliente(createIdRequest(1L));
        pedidoRequestDTO.setRestaurante(createIdRequest(1L));
        pedidoRequestDTO.setItens(Collections.singletonList(itemDTO));
    }

    @Test
    void criar_ComDadosValidos_DeveCriarPedido() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PedidoResponseDTO response = pedidoService.criar(pedidoRequestDTO);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(StatusPedido.PENDENTE);
        assertThat(response.getValorTotal()).isEqualTo(new BigDecimal("55.00")); // (2 * 20) + 5
        verify(produtoRepository, times(1)).save(any(Produto.class));
        assertThat(produto.getQuantidadeEstoque()).isEqualTo(8);
    }

    @Test
    void criar_ComEstoqueInsuficiente_DeveLancarBusinessException() {
        produto.setQuantidadeEstoque(1); // Apenas 1 no estoque
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        assertThrows(BusinessException.class, () -> {
            pedidoService.criar(pedidoRequestDTO);
        });

        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void alterarStatus_ComTransicaoValida_DeveAtualizarStatus() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setStatus(StatusPedido.PENDENTE);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        PedidoResponseDTO response = pedidoService.alterarStatus(1L, StatusPedido.CONFIRMADO);

        assertThat(response.getStatus()).isEqualTo(StatusPedido.CONFIRMADO);
    }

    @Test
    void alterarStatus_ComTransicaoInvalida_DeveLancarBusinessException() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setStatus(StatusPedido.ENTREGUE);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(BusinessException.class, () -> {
            pedidoService.alterarStatus(1L, StatusPedido.CONFIRMADO);
        });
    }
}
