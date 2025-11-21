package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.IdRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.ItemPedidoRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.PedidoRequestDTO;
import com.delivery_api.Projeto.Delivery.API.entity.Cliente;
import com.delivery_api.Projeto.Delivery.API.entity.Produto;
import com.delivery_api.Projeto.Delivery.API.entity.Restaurante;
import com.delivery_api.Projeto.Delivery.API.repository.ClienteRepository;
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
import java.util.List;

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

    private Cliente cliente;
    private Restaurante restaurante;
    private Produto produto;

    @BeforeEach
    void setUp() {
        // Cria entidades reutilizáveis para os testes
        cliente = new Cliente();
        cliente.setNome("Cliente Pedido Teste");
        cliente.setEmail("pedido@teste.com");
        cliente.setTelefone("987654321");
        cliente.setEndereco("Rua do Pedido, 456");
        cliente.setAtivo(true);
        cliente = clienteRepository.save(cliente);

        restaurante = new Restaurante();
        restaurante.setNome("Restaurante Pedido Teste");
        restaurante.setTaxaEntrega(new BigDecimal("5.00"));
        restaurante.setAtivo(true);
        restaurante = restauranteRepository.save(restaurante);

        produto = new Produto();
        produto.setNome("Produto Pedido Teste");
        produto.setPreco(new BigDecimal("10.00"));
        produto.setRestaurante(restaurante);
        produto.setDisponivel(true);
        produto = produtoRepository.save(produto);
    }

    @Test
    void criar_deveRetornar201_quandoDadosValidos() throws Exception {
        // Arrange
        ItemPedidoRequestDTO itemRequest = new ItemPedidoRequestDTO();
        itemRequest.setProduto(new IdRequestDTO());
        itemRequest.setQuantidade(2);

        PedidoRequestDTO pedidoRequest = new PedidoRequestDTO();
        pedidoRequest.setCliente(new IdRequestDTO());
        pedidoRequest.setRestaurante(new IdRequestDTO());
        pedidoRequest.setEnderecoEntrega("Endereço de entrega do teste");
        pedidoRequest.setItens(List.of(itemRequest));

        // Act & Assert
        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedidoRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.clienteId").value(cliente.getId()))
                .andExpect(jsonPath("$.data.restauranteId").value(restaurante.getId()))
                .andExpect(jsonPath("$.data.valorTotal").value(25.00)) // (2 * 10.00) + 5.00 taxa
                .andExpect(jsonPath("$.data.itens[0].produtoId").value(produto.getId()));
    }
}
