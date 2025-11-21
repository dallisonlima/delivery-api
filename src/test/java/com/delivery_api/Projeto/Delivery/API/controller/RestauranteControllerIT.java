package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.entity.Restaurante;
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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class RestauranteControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @BeforeEach
    void setUp() {
        // Cria restaurantes para os testes de filtro e paginação
        Restaurante r1 = new Restaurante();
        r1.setNome("Pizzaria Teste");
        r1.setCategoria("Pizza");
        r1.setTaxaEntrega(new BigDecimal("3.00"));
        r1.setAtivo(true);
        restauranteRepository.save(r1);

        Restaurante r2 = new Restaurante();
        r2.setNome("Hamburgueria Teste");
        r2.setCategoria("Lanches");
        r2.setTaxaEntrega(new BigDecimal("4.00"));
        r2.setAtivo(true);
        restauranteRepository.save(r2);

        Restaurante r3 = new Restaurante();
        r3.setNome("Japonês Teste");
        r3.setCategoria("Japonesa");
        r3.setTaxaEntrega(new BigDecimal("10.00"));
        r3.setAtivo(false); // Inativo
        restauranteRepository.save(r3);
    }

    @Test
    void listar_deveRetornarPaginaDeRestaurantes_quandoSemFiltros() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/restaurantes")
                        .param("page", "0")
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.page.totalElements").value(3))
                .andExpect(jsonPath("$.page.totalPages").value(2))
                .andExpect(jsonPath("$.links.first").exists())
                .andExpect(jsonPath("$.links.last").exists());
    }

    @Test
    void listar_deveRetornarRestaurantesFiltradosPorCategoria_quandoFiltroAplicado() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/restaurantes")
                        .param("categoria", "Pizza")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nome").value("Pizzaria Teste"))
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    @Test
    void listar_deveRetornarRestaurantesFiltradosPorStatus_quandoFiltroAplicado() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/restaurantes")
                        .param("ativo", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nome").value("Japonês Teste"))
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }
}
