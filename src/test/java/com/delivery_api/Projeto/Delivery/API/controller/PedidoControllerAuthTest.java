package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.IdRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.PedidoRequestDTO;
import com.delivery_api.Projeto.Delivery.API.entity.*;
import com.delivery_api.Projeto.Delivery.API.enums.Role;
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

import java.time.LocalDateTime;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PedidoControllerAuthTest {

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
    private UsuarioRepository usuarioRepository;

    private Pedido pedido;
    private Cliente cliente;
    private Restaurante restaurante;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setAtivo(true);
        cliente = clienteRepository.save(cliente);

        restaurante = new Restaurante();
        restaurante = restauranteRepository.save(restaurante);

        pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido = pedidoRepository.save(pedido);

        Usuario clientUser = new Usuario(cliente.getId(), "client@example.com", "pw", "Cliente", Role.CLIENTE, true, LocalDateTime.now(), null);
        Usuario restaurantUser = new Usuario(null, "restaurant@example.com", "pw", "Restaurante", Role.RESTAURANTE, true, LocalDateTime.now(), restaurante.getId());
        usuarioRepository.save(clientUser);
        usuarioRepository.save(restaurantUser);
    }
    
    @AfterEach
    void tearDown() {
        pedidoRepository.deleteAll();
        clienteRepository.deleteAll();
        restauranteRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    private PedidoRequestDTO createValidPedidoRequestDTO() {
        IdRequestDTO clienteId = new IdRequestDTO();
        clienteId.setId(cliente.getId());

        IdRequestDTO restauranteId = new IdRequestDTO();
        restauranteId.setId(restaurante.getId());

        PedidoRequestDTO dto = new PedidoRequestDTO();
        dto.setCliente(clienteId);
        dto.setRestaurante(restauranteId);
        dto.setItens(Collections.emptyList());
        return dto;
    }

    // --- GET /api/pedidos ---
    @Test
    @WithMockUser(roles = "ADMIN")
    void listar_ShouldAllow_WhenUserIsAdmin() throws Exception {
        mockMvc.perform(get("/api/pedidos")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void listar_ShouldForbid_WhenUserIsCliente() throws Exception {
        mockMvc.perform(get("/api/pedidos")).andExpect(status().isForbidden());
    }

    // --- GET /api/pedidos/meus ---
    @Test
    @WithMockUser(roles = "CLIENTE")
    void listarMeusPedidos_ShouldAllow_WhenUserIsCliente() throws Exception {
        mockMvc.perform(get("/api/pedidos/meus")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listarMeusPedidos_ShouldForbid_WhenUserIsAdmin() throws Exception {
        mockMvc.perform(get("/api/pedidos/meus")).andExpect(status().isForbidden());
    }

    // --- GET /api/pedidos/restaurante ---
    @Test
    @WithMockUser(roles = "RESTAURANTE")
    void listarPedidosRestaurante_ShouldAllow_WhenUserIsRestaurante() throws Exception {
        mockMvc.perform(get("/api/pedidos/restaurante")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void listarPedidosRestaurante_ShouldForbid_WhenUserIsCliente() throws Exception {
        mockMvc.perform(get("/api/pedidos/restaurante")).andExpect(status().isForbidden());
    }

    // --- POST /api/pedidos ---
    @Test
    @WithMockUser(roles = "CLIENTE")
    void criar_ShouldAllow_WhenUserIsCliente() throws Exception {
        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createValidPedidoRequestDTO())))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void criar_ShouldForbid_WhenUserIsAdmin() throws Exception {
        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createValidPedidoRequestDTO())))
                .andExpect(status().isForbidden());
    }

    // --- GET /api/pedidos/{id} ---
    @Test
    @WithMockUser(roles = "ADMIN")
    void buscarPorId_ShouldAllow_WhenUserIsAdmin() throws Exception {
        mockMvc.perform(get("/api/pedidos/" + pedido.getId())).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "client@example.com", roles = "CLIENTE")
    void buscarPorId_ShouldAllow_WhenUserIsClientOwner() throws Exception {
        mockMvc.perform(get("/api/pedidos/" + pedido.getId())).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "restaurant@example.com", roles = "RESTAURANTE")
    void buscarPorId_ShouldAllow_WhenUserIsRestaurantOwner() throws Exception {
        mockMvc.perform(get("/api/pedidos/" + pedido.getId())).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "other@example.com", roles = "CLIENTE")
    void buscarPorId_ShouldForbid_WhenUserIsNotOwner() throws Exception {
        Usuario otherClient = new Usuario(999L, "other@example.com", "pw", "Outro", Role.CLIENTE, true, LocalDateTime.now(), null);
        usuarioRepository.save(otherClient);
        mockMvc.perform(get("/api/pedidos/" + pedido.getId())).andExpect(status().isForbidden());
    }
}
