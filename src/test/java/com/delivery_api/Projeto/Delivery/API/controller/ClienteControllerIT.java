package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.ClienteRequestDTO;
import com.delivery_api.Projeto.Delivery.API.entity.Cliente;
import com.delivery_api.Projeto.Delivery.API.repository.ClienteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser // Garante que todos os testes nesta classe rodem com um usuário autenticado
class ClienteControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClienteRepository clienteRepository;

    @AfterEach
    void tearDown() {
        clienteRepository.deleteAll();
    }

    private ClienteRequestDTO createValidClienteRequestDTO() {
        ClienteRequestDTO dto = new ClienteRequestDTO();
        dto.setNome("Cliente Válido");
        dto.setEmail("valid@example.com");
        dto.setTelefone("11987654321");
        dto.setCep("12345-678");
        dto.setLogradouro("Rua Teste");
        dto.setNumero("123");
        dto.setBairro("Bairro Teste");
        dto.setCidade("Cidade Teste");
        dto.setEstado("SP");
        return dto;
    }

    @Test
    void cadastrar_ComDadosValidos_DeveRetornar201ECliente() throws Exception {
        ClienteRequestDTO dto = createValidClienteRequestDTO();

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.nome").value("Cliente Válido"))
                .andExpect(jsonPath("$.data.email").value("valid@example.com"));
    }

    @Test
    void cadastrar_ComDadosInvalidos_DeveRetornar400() throws Exception {
        ClienteRequestDTO dto = createValidClienteRequestDTO();
        dto.setNome(""); // Nome inválido

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buscarPorId_QuandoClienteExiste_DeveRetornar200ECliente() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setNome("Cliente Existente");
        cliente.setEmail("existente@example.com");
        cliente = clienteRepository.save(cliente);

        mockMvc.perform(get("/api/clientes/" + cliente.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(cliente.getId()))
                .andExpect(jsonPath("$.data.nome").value("Cliente Existente"));
    }

    @Test
    void buscarPorId_QuandoClienteNaoExiste_DeveRetornar404() throws Exception {
        mockMvc.perform(get("/api/clientes/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listarAtivos_DeveRetornar200EListaDeClientes() throws Exception {
        Cliente cliente1 = new Cliente();
        cliente1.setNome("Cliente 1");
        cliente1.setEmail("c1@example.com");
        cliente1.setAtivo(true);
        clienteRepository.save(cliente1);

        Cliente cliente2 = new Cliente();
        cliente2.setNome("Cliente 2");
        cliente2.setEmail("c2@example.com");
        cliente2.setAtivo(true);
        clienteRepository.save(cliente2);

        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Test
    void atualizar_ComDadosValidos_DeveRetornar200EClienteAtualizado() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setNome("Cliente Original");
        cliente.setEmail("original@example.com");
        cliente = clienteRepository.save(cliente);

        ClienteRequestDTO dto = createValidClienteRequestDTO();
        dto.setNome("Cliente Atualizado");
        dto.setEmail("atualizado@example.com");

        mockMvc.perform(put("/api/clientes/" + cliente.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(cliente.getId()))
                .andExpect(jsonPath("$.data.nome").value("Cliente Atualizado"));
    }
}
