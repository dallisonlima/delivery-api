package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.ProdutoRequestDTO;
import com.delivery_api.Projeto.Delivery.API.entity.Cliente;
import com.delivery_api.Projeto.Delivery.API.entity.ItemPedido;
import com.delivery_api.Projeto.Delivery.API.entity.Pedido;
import com.delivery_api.Projeto.Delivery.API.entity.Produto;
import com.delivery_api.Projeto.Delivery.API.entity.Restaurante;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ProdutoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    private Restaurante restaurante;
    private Produto produto;

    @BeforeEach
    void setUp() {
        restaurante = new Restaurante();
        restaurante.setNome("Restaurante para Produtos");
        restaurante.setTaxaEntrega(new BigDecimal("1.00"));
        restaurante.setAtivo(true);
        restaurante = restauranteRepository.save(restaurante);

        produto = new Produto();
        produto.setNome("Produto Base");
        produto.setPreco(new BigDecimal("20.00"));
        produto.setCategoria("Bebidas");
        produto.setRestaurante(restaurante);
        produto.setDisponivel(true);
        produto = produtoRepository.save(produto);
    }

    @Test
    void cadastrar_deveRetornar201_quandoDadosValidos() throws Exception {
        ProdutoRequestDTO requestDTO = new ProdutoRequestDTO();
        requestDTO.setNome("Novo Produto");
        requestDTO.setDescricao("Uma descrição válida com mais de dez caracteres.");
        requestDTO.setPreco(new BigDecimal("15.50"));
        requestDTO.setCategoria("Lanches");
        requestDTO.setRestauranteId(restaurante.getId());
        requestDTO.setDisponivel(true);

        mockMvc.perform(post("/api/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.data.nome").value("Novo Produto"));
    }

    @Test
    void cadastrar_deveRetornar400_quandoDadosInvalidos() throws Exception {
        ProdutoRequestDTO requestDTO = new ProdutoRequestDTO();
        requestDTO.setNome(""); // Nome inválido

        mockMvc.perform(post("/api/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cadastrar_deveRetornar400_quandoPrecoInvalido() throws Exception {
        ProdutoRequestDTO requestDTO = new ProdutoRequestDTO();
        requestDTO.setNome("Produto com Preço Inválido");
        requestDTO.setDescricao("Uma descrição válida com mais de dez caracteres.");
        requestDTO.setPreco(new BigDecimal("-10.00")); // Preço negativo
        requestDTO.setCategoria("Lanches");
        requestDTO.setRestauranteId(restaurante.getId());
        requestDTO.setDisponivel(true);

        mockMvc.perform(post("/api/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deletar_deveRetornar409_quandoProdutoEstaEmPedido() throws Exception {
        Cliente cliente = clienteRepository.save(new Cliente());
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setDataPedido(LocalDateTime.now());
        ItemPedido item = new ItemPedido();
        item.setProduto(produto);
        item.setPedido(pedido);
        item.setQuantidade(1);
        pedido.setItens(List.of(item));
        pedidoRepository.save(pedido);

        mockMvc.perform(delete("/api/produtos/{id}", produto.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void buscarPorCategoria_deveRetornarPaginaDeProdutos() throws Exception {
        mockMvc.perform(get("/api/produtos/categoria/{categoria}", "Bebidas")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].nome").value("Produto Base"));
    }

    @Test
    void buscarPorId_deveRetornar200_quandoProdutoExiste() throws Exception {
        mockMvc.perform(get("/api/produtos/{id}", produto.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(produto.getId()));
    }

    @Test
    void atualizar_deveRetornar200_quandoDadosValidos() throws Exception {
        ProdutoRequestDTO requestDTO = new ProdutoRequestDTO();
        requestDTO.setNome("Produto Atualizado");
        requestDTO.setDescricao("Uma descrição válida e atualizada com mais de dez caracteres.");
        requestDTO.setPreco(new BigDecimal("25.00"));
        requestDTO.setCategoria("Bebidas");
        requestDTO.setRestauranteId(restaurante.getId());
        requestDTO.setDisponivel(true);

        mockMvc.perform(put("/api/produtos/{id}", produto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nome").value("Produto Atualizado"));
    }

    @Test
    void deletar_deveRetornar204_quandoProdutoExiste() throws Exception {
        mockMvc.perform(delete("/api/produtos/{id}", produto.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void buscarPorId_deveRetornar404_quandoProdutoNaoExiste() throws Exception {
        mockMvc.perform(get("/api/produtos/{id}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
