package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.ClienteRequestDTO;
import com.delivery_api.Projeto.Delivery.API.entity.Cliente;
import com.delivery_api.Projeto.Delivery.API.repository.ClienteRepository;
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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ClienteControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClienteRepository clienteRepository;

    private Cliente clienteExistente;

    @BeforeEach
    void setUp() {
        clienteExistente = new Cliente();
        clienteExistente.setEmail("existente@email.com");
        clienteExistente.setNome("Cliente Existente");
        clienteExistente.setTelefone("111111111");
        clienteExistente.setEndereco("Endereço Existente");
        clienteExistente.setAtivo(true);
        clienteRepository.save(clienteExistente);
    }

    @Test
    void cadastrar_deveRetornar201_quandoDadosValidos() throws Exception {
        ClienteRequestDTO clienteRequest = new ClienteRequestDTO();
        clienteRequest.setNome("Cliente Teste");
        clienteRequest.setEmail("teste@email.com");
        clienteRequest.setTelefone("123456789");
        clienteRequest.setEndereco("Rua dos Testes, 123");

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nome").value("Cliente Teste"));
    }

    @Test
    void cadastrar_deveRetornar400_quandoDadosInvalidos() throws Exception {
        ClienteRequestDTO clienteRequest = new ClienteRequestDTO();
        clienteRequest.setNome("");
        clienteRequest.setEmail("emailinvalido");

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Erro de validação"));
    }

    @Test
    void cadastrar_deveRetornar409_quandoEmailJaExiste() throws Exception {
        ClienteRequestDTO clienteRequest = new ClienteRequestDTO();
        clienteRequest.setNome("Novo Cliente");
        clienteRequest.setEmail("existente@email.com");
        clienteRequest.setTelefone("222222222");
        clienteRequest.setEndereco("Novo Endereço");

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email já cadastrado: existente@email.com"));
    }

    @Test
    void buscarPorId_deveRetornar200_quandoClienteExiste() throws Exception {
        mockMvc.perform(get("/api/clientes/{id}", clienteExistente.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(clienteExistente.getId()))
                .andExpect(jsonPath("$.data.nome").value("Cliente Existente"));
    }

    @Test
    void buscarPorId_deveRetornar404_quandoClienteNaoExiste() throws Exception {
        mockMvc.perform(get("/api/clientes/{id}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cliente não encontrado: 9999"));
    }

    @Test
    void listarAtivos_deveRetornarPaginaDeClientes() throws Exception {
        Cliente outroCliente = new Cliente();
        outroCliente.setEmail("outro@email.com");
        outroCliente.setNome("Outro Cliente");
        outroCliente.setTelefone("333333333");
        outroCliente.setEndereco("Outro Endereço");
        outroCliente.setAtivo(true);
        clienteRepository.save(outroCliente);

        mockMvc.perform(get("/api/clientes")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.links.first").exists());
    }
}
