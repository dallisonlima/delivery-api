package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.RestauranteRequestDTO;
import com.delivery_api.Projeto.Delivery.API.entity.Restaurante;
import com.delivery_api.Projeto.Delivery.API.entity.Usuario;
import com.delivery_api.Projeto.Delivery.API.enums.Role;
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
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RestauranteControllerAuthTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Restaurante restaurante;

    @BeforeEach
    void setUp() {
        // Criar um restaurante para os testes de PUT/DELETE
        restaurante = new Restaurante();
        restaurante.setNome("Restaurante Teste");
        restaurante.setTaxaEntrega(BigDecimal.TEN);
        restaurante = restauranteRepository.save(restaurante);

        // Criar um usuário dono do restaurante
        Usuario owner = new Usuario(null, "owner@example.com", "password", "Dono", Role.RESTAURANTE, true, LocalDateTime.now(), restaurante.getId());
        usuarioRepository.save(owner);
    }
    
    @AfterEach
    void tearDown() {
        restauranteRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    private RestauranteRequestDTO createRestauranteRequestDTO() {
        RestauranteRequestDTO dto = new RestauranteRequestDTO();
        dto.setNome("Novo Restaurante");
        dto.setTaxaEntrega(BigDecimal.ONE);
        return dto;
    }

    // --- GET /api/restaurantes (Public) ---
    @Test
    @WithAnonymousUser
    void listar_ShouldAllow_WhenUserIsAnonymous() throws Exception {
        mockMvc.perform(get("/api/restaurantes"))
                .andExpect(status().isOk());
    }

    // --- POST /api/restaurantes ---
    @Test
    @WithMockUser(roles = "ADMIN")
    void cadastrar_ShouldAllow_WhenUserIsAdmin() throws Exception {
        mockMvc.perform(post("/api/restaurantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRestauranteRequestDTO())))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void cadastrar_ShouldForbid_WhenUserIsCliente() throws Exception {
        mockMvc.perform(post("/api/restaurantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRestauranteRequestDTO())))
                .andExpect(status().isForbidden());
    }

    // --- PUT /api/restaurantes/{id} ---
    @Test
    @WithMockUser(roles = "ADMIN")
    void atualizar_ShouldAllow_WhenUserIsAdmin() throws Exception {
        mockMvc.perform(put("/api/restaurantes/" + restaurante.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRestauranteRequestDTO())))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "owner@example.com", roles = "RESTAURANTE")
    void atualizar_ShouldAllow_WhenUserIsOwner() throws Exception {
        mockMvc.perform(put("/api/restaurantes/" + restaurante.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRestauranteRequestDTO())))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "another_owner@example.com", roles = "RESTAURANTE")
    void atualizar_ShouldForbid_WhenUserIsNotOwner() throws Exception {
        // Criar um usuário que não é o dono
        Usuario notOwner = new Usuario(null, "another_owner@example.com", "password", "Outro Dono", Role.RESTAURANTE, true, LocalDateTime.now(), 999L);
        usuarioRepository.save(notOwner);

        mockMvc.perform(put("/api/restaurantes/" + restaurante.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRestauranteRequestDTO())))
                .andExpect(status().isForbidden());
    }
}
