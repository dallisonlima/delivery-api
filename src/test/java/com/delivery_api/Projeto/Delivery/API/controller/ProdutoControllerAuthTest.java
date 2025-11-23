package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.request.ProdutoRequestDTO;
import com.delivery_api.Projeto.Delivery.API.entity.Produto;
import com.delivery_api.Projeto.Delivery.API.entity.Restaurante;
import com.delivery_api.Projeto.Delivery.API.entity.Usuario;
import com.delivery_api.Projeto.Delivery.API.enums.Role;
import com.delivery_api.Projeto.Delivery.API.repository.ProdutoRepository;
import com.delivery_api.Projeto.Delivery.API.repository.RestauranteRepository;
import com.delivery_api.Projeto.Delivery.API.repository.UsuarioRepository;
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
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProdutoControllerAuthTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Restaurante restaurante;
    private Produto produto;

    @BeforeEach
    void setUp() {
        restaurante = new Restaurante();
        restaurante.setNome("Restaurante do Dono");
        restaurante = restauranteRepository.save(restaurante);

        produto = new Produto();
        produto.setNome("Produto Teste");
        produto.setRestaurante(restaurante);
        produto.setPreco(BigDecimal.TEN);
        produto = produtoRepository.save(produto);

        Usuario owner = new Usuario(null, "product_owner@example.com", "password", "Dono Produto", Role.RESTAURANTE, true, LocalDateTime.now(), restaurante.getId());
        usuarioRepository.save(owner);
    }
    
    @AfterEach
    void tearDown() {
        produtoRepository.deleteAll();
        restauranteRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    private ProdutoRequestDTO createProdutoRequestDTO() {
        ProdutoRequestDTO dto = new ProdutoRequestDTO();
        dto.setNome("Novo Produto");
        dto.setPreco(BigDecimal.ONE);
        dto.setRestauranteId(restaurante.getId());
        dto.setDisponivel(true);
        return dto;
    }

    // --- POST /api/produtos ---
    @Test
    @WithMockUser(roles = "RESTAURANTE")
    void cadastrar_ShouldAllow_WhenUserIsRestaurante() throws Exception {
        mockMvc.perform(post("/api/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createProdutoRequestDTO())))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void cadastrar_ShouldAllow_WhenUserIsAdmin() throws Exception {
        mockMvc.perform(post("/api/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createProdutoRequestDTO())))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void cadastrar_ShouldForbid_WhenUserIsCliente() throws Exception {
        mockMvc.perform(post("/api/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createProdutoRequestDTO())))
                .andExpect(status().isForbidden());
    }

    // --- PUT /api/produtos/{id} ---
    @Test
    @WithMockUser(roles = "ADMIN")
    void atualizar_ShouldAllow_WhenUserIsAdmin() throws Exception {
        mockMvc.perform(put("/api/produtos/" + produto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createProdutoRequestDTO())))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "product_owner@example.com", roles = "RESTAURANTE")
    void atualizar_ShouldAllow_WhenUserIsOwner() throws Exception {
        mockMvc.perform(put("/api/produtos/" + produto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createProdutoRequestDTO())))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "another_user@example.com", roles = "RESTAURANTE")
    void atualizar_ShouldForbid_WhenUserIsNotOwner() throws Exception {
        Usuario notOwner = new Usuario(null, "another_user@example.com", "password", "Não Dono", Role.RESTAURANTE, true, LocalDateTime.now(), 999L);
        usuarioRepository.save(notOwner);

        mockMvc.perform(put("/api/produtos/" + produto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createProdutoRequestDTO())))
                .andExpect(status().isForbidden());
    }

    // --- DELETE /api/produtos/{id} ---
    @Test
    @WithMockUser(roles = "ADMIN")
    void deletar_ShouldAllow_WhenUserIsAdmin() throws Exception {
        mockMvc.perform(delete("/api/produtos/" + produto.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "product_owner@example.com", roles = "RESTAURANTE")
    void deletar_ShouldAllow_WhenUserIsOwner() throws Exception {
        mockMvc.perform(delete("/api/produtos/" + produto.getId()))
                .andExpect(status().isNoContent());
    }
}
