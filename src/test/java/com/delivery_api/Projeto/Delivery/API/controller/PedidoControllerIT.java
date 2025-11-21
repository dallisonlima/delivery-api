package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.IdRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.ItemPedidoRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.PedidoRequestDTO;
import com.delivery_api.Projeto.Delivery.API.entity.*;
import com.delivery_api.Projeto.Delivery.API.repository.ClienteRepository;
import com.delivery_api.Projeto.Delivery.API.repository.PedidoRepository;
import com.delivery_api.Projeto.Delivery.API.repository.ProdutoRepository;
import com.delivery_api.Projeto.Delivery.API.repository.RestauranteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class PedidoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    private Cliente cliente;
    private Restaurante restaurante;
    private Produto produto;
    private Pedido pedido;

    @BeforeEach
    void setUp() {
        cliente = clienteRepository.save(new Cliente("Cliente Pedido Teste", "pedido@teste.com", "987654321", "Rua do Pedido, 456", true));
        restaurante = restauranteRepository.save(new Restaurante("Restaurante Pedido Teste", new BigDecimal("5.00"), "Lanches", true, "Endereço", "12345", 4.5));
        produto = produtoRepository.save(new Produto("Produto Pedido Teste", "Desc", new BigDecimal("10.00"), "Bebidas", true, restaurante));

        pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido = pedidoRepository.save(pedido);
    }

    @Test
    void criar_deveRetornar201_quandoDadosValidos() throws Exception {
        ItemPedidoRequestDTO itemRequest = new ItemPedidoRequestDTO(new IdRequestDTO(produto.getId()), 2);
        PedidoRequestDTO pedidoRequest = new PedidoRequestDTO(new IdRequestDTO(cliente.getId()), new IdRequestDTO(restaurante.getId()), "Endereço", List.of(itemRequest));

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedidoRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.data.valorTotal").value(25.00));
    }

    @Test
    void criar_deveRetornar409_quandoClienteInativo() throws Exception {
        cliente.setAtivo(false);
        clienteRepository.save(cliente);

        ItemPedidoRequestDTO itemRequest = new ItemPedidoRequestDTO(new IdRequestDTO(produto.getId()), 1);
        PedidoRequestDTO pedidoRequest = new PedidoRequestDTO(new IdRequestDTO(cliente.getId()), new IdRequestDTO(restaurante.getId()), "Endereço", List.of(itemRequest));

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedidoRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void buscarPorId_deveRetornar200_quandoPedidoExiste() throws Exception {
        mockMvc.perform(get("/api/pedidos/{id}", pedido.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(pedido.getId()));
    }

    @Test
    void buscarPorId_deveRetornar404_quandoPedidoNaoExiste() throws Exception {
        mockMvc.perform(get("/api/pedidos/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void listar_deveRetornarPaginaDePedidos() throws Exception {
        mockMvc.perform(get("/api/pedidos")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    @Test
    void alterarStatus_deveRetornar400_quandoTransicaoInvalida() throws Exception {
        pedido.setStatus(StatusPedido.ENTREGUE);
        pedidoRepository.save(pedido);

        mockMvc.perform(patch("/api/pedidos/{id}/status", pedido.getId())
                        .param("status", "CANCELADO"))
                .andExpect(status().isBadRequest());
    }
}
