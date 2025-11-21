package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.ProdutoRequestDTO;
import com.delivery_api.Projeto.Delivery.API.entity.Produto;
import com.delivery_api.Projeto.Delivery.API.entity.Restaurante;
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
        produto.setRestaurante(restaurante);
        produto.setDisponivel(true);
        produto = produtoRepository.save(produto);
    }

    @Test
    void cadastrar_deveRetornar201_quandoDadosValidos() throws Exception {
        // Arrange
        ProdutoRequestDTO requestDTO = new ProdutoRequestDTO();
        requestDTO.setNome("Novo Produto");
        requestDTO.setPreco(new BigDecimal("15.50"));
        requestDTO.setRestauranteId(restaurante.getId());
        requestDTO.setDisponivel(true);

        // Act & Assert
        mockMvc.perform(post("/api/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.data.nome").value("Novo Produto"));
    }

    @Test
    void buscarPorId_deveRetornar200_quandoProdutoExiste() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/produtos/{id}", produto.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(produto.getId()))
                .andExpect(jsonPath("$.data.nome").value("Produto Base"));
    }

    @Test
    void atualizar_deveRetornar200_quandoDadosValidos() throws Exception {
        // Arrange
        ProdutoRequestDTO requestDTO = new ProdutoRequestDTO();
        requestDTO.setNome("Produto Atualizado");
        requestDTO.setPreco(new BigDecimal("25.00"));
        requestDTO.setRestauranteId(restaurante.getId());
        requestDTO.setDisponivel(true);

        // Act & Assert
        mockMvc.perform(put("/api/produtos/{id}", produto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nome").value("Produto Atualizado"))
                .andExpect(jsonPath("$.data.preco").value(25.00));
    }

    @Test
    void deletar_deveRetornar204_quandoProdutoExiste() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/produtos/{id}", produto.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void buscarPorId_deveRetornar404_quandoProdutoNaoExiste() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/produtos/{id}", 9999L) // ID inexistente
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
