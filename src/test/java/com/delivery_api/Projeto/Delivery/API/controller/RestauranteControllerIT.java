package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.RestauranteRequestDTO;
import com.delivery_api.Projeto.Delivery.API.entity.Cliente;
import com.delivery_api.Projeto.Delivery.API.entity.Pedido;
import com.delivery_api.Projeto.Delivery.API.entity.Restaurante;
import com.delivery_api.Projeto.Delivery.API.repository.ClienteRepository;
import com.delivery_api.Projeto.Delivery.API.repository.PedidoRepository;
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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class RestauranteControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    private Restaurante restaurantePizza;

    @BeforeEach
    void setUp() {
        restaurantePizza = new Restaurante();
        restaurantePizza.setNome("Pizzaria Teste");
        restaurantePizza.setCategoria("Pizza");
        restaurantePizza.setTaxaEntrega(new BigDecimal("3.00"));
        restaurantePizza.setAtivo(true);
        restauranteRepository.save(restaurantePizza);

        Restaurante restauranteLanches = new Restaurante();
        restauranteLanches.setNome("Hamburgueria Teste");
        restauranteLanches.setCategoria("Lanches");
        restauranteLanches.setTaxaEntrega(new BigDecimal("4.00"));
        restauranteLanches.setAtivo(true);
        restauranteRepository.save(restauranteLanches);

        Restaurante restauranteJapones = new Restaurante();
        restauranteJapones.setNome("Japonês Teste");
        restauranteJapones.setCategoria("Japonesa");
        restauranteJapones.setTaxaEntrega(new BigDecimal("10.00"));
        restauranteJapones.setAtivo(false);
        restauranteRepository.save(restauranteJapones);
    }

    @Test
    void cadastrar_deveRetornar201_quandoDadosValidos() throws Exception {
        RestauranteRequestDTO requestDTO = new RestauranteRequestDTO();
        requestDTO.setNome("Restaurante Novo");
        requestDTO.setTaxaEntrega(new BigDecimal("5.00"));
        requestDTO.setCategoria("Brasileira");

        mockMvc.perform(post("/api/restaurantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.data.nome").value("Restaurante Novo"));
    }

    @Test
    void cadastrar_deveRetornar400_quandoDadosInvalidos() throws Exception {
        RestauranteRequestDTO requestDTO = new RestauranteRequestDTO();
        requestDTO.setNome("");

        mockMvc.perform(post("/api/restaurantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buscarPorId_deveRetornar200_quandoRestauranteExiste() throws Exception {
        mockMvc.perform(get("/api/restaurantes/{id}", restaurantePizza.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(restaurantePizza.getId()));
    }

    @Test
    void buscarPorId_deveRetornar404_quandoRestauranteNaoExiste() throws Exception {
        mockMvc.perform(get("/api/restaurantes/{id}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletar_deveRetornar409_quandoRestauranteTemPedidos() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setNome("Cliente");
        cliente.setEmail("c@c.com");
        clienteRepository.save(cliente);

        Pedido pedido = new Pedido();
        pedido.setRestaurante(restaurantePizza);
        pedido.setCliente(cliente);
        pedido.setDataPedido(LocalDateTime.now());
        pedidoRepository.save(pedido);

        mockMvc.perform(delete("/api/restaurantes/{id}", restaurantePizza.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void listar_deveRetornarPaginaDeRestaurantes_quandoSemFiltros() throws Exception {
        mockMvc.perform(get("/api/restaurantes")
                        .param("page", "0")
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.page.totalElements").value(3))
                .andExpect(jsonPath("$.page.totalPages").value(2));
    }

    @Test
    void listar_deveRetornarRestaurantesFiltradosPorCategoria_quandoFiltroAplicado() throws Exception {
        mockMvc.perform(get("/api/restaurantes")
                        .param("categoria", "Pizza")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nome").value("Pizzaria Teste"));
    }

    @Test
    void listar_deveRetornarRestaurantesFiltradosPorStatus_quandoFiltroAplicado() throws Exception {
        mockMvc.perform(get("/api/restaurantes")
                        .param("ativo", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nome").value("Japonês Teste"));
    }
}
