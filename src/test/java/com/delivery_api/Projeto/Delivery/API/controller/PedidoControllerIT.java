package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.IdRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.ItemPedidoRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.PedidoRequestDTO;
import com.delivery_api.Projeto.Delivery.API.entity.*;
import com.delivery_api.Projeto.Delivery.API.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(roles = "CLIENTE") // A maioria das operações de teste são como cliente
class PedidoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private RestauranteRepository restauranteRepository;
    @Autowired
    private ProdutoRepository produtoRepository;

    private Cliente cliente;
    private Restaurante restaurante;
    private Produto produto;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setAtivo(true);
        cliente = clienteRepository.save(cliente);

        restaurante = new Restaurante();
        restaurante.setTaxaEntrega(BigDecimal.valueOf(10));
        restaurante = restauranteRepository.save(restaurante);

        produto = new Produto();
        produto.setRestaurante(restaurante);
        produto.setDisponivel(true);
        produto.setPreco(BigDecimal.valueOf(25));
        produto.setQuantidadeEstoque(10);
        produto = produtoRepository.save(produto);
    }

    @AfterEach
    void tearDown() {
        pedidoRepository.deleteAll();
        produtoRepository.deleteAll();
        restauranteRepository.deleteAll();
        clienteRepository.deleteAll();
    }

    private IdRequestDTO createIdRequest(Long id) {
        IdRequestDTO idRequest = new IdRequestDTO();
        idRequest.setId(id);
        return idRequest;
    }

    @Test
    void criar_ComDadosValidos_DeveRetornar201ECalcularTotalCorretamente() throws Exception {
        ItemPedidoRequestDTO itemDTO = new ItemPedidoRequestDTO();
        itemDTO.setProduto(createIdRequest(produto.getId()));
        itemDTO.setQuantidade(2);

        PedidoRequestDTO pedidoDTO = new PedidoRequestDTO();
        pedidoDTO.setCliente(createIdRequest(cliente.getId()));
        pedidoDTO.setRestaurante(createIdRequest(restaurante.getId()));
        pedidoDTO.setItens(Collections.singletonList(itemDTO));
        pedidoDTO.setEnderecoEntrega("Rua Teste, 123");

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedidoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.valorTotal").value(60.00)); // (2 * 25) + 10
    }

    @Test
    void criar_ComProdutoInexistente_DeveRetornar404() throws Exception {
        ItemPedidoRequestDTO itemDTO = new ItemPedidoRequestDTO();
        itemDTO.setProduto(createIdRequest(999L)); // ID inexistente
        itemDTO.setQuantidade(1);

        PedidoRequestDTO pedidoDTO = new PedidoRequestDTO();
        pedidoDTO.setCliente(createIdRequest(cliente.getId()));
        pedidoDTO.setRestaurante(createIdRequest(restaurante.getId()));
        pedidoDTO.setItens(Collections.singletonList(itemDTO));
        pedidoDTO.setEnderecoEntrega("Rua Teste, 123");

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedidoDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void criar_ComEstoqueInsuficiente_DeveRetornar400() throws Exception {
        ItemPedidoRequestDTO itemDTO = new ItemPedidoRequestDTO();
        itemDTO.setProduto(createIdRequest(produto.getId()));
        itemDTO.setQuantidade(11); // Mais do que o estoque de 10

        PedidoRequestDTO pedidoDTO = new PedidoRequestDTO();
        pedidoDTO.setCliente(createIdRequest(cliente.getId()));
        pedidoDTO.setRestaurante(createIdRequest(restaurante.getId()));
        pedidoDTO.setItens(Collections.singletonList(itemDTO));
        pedidoDTO.setEnderecoEntrega("Rua Teste, 123");

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedidoDTO)))
                .andExpect(status().isBadRequest());
    }
}
