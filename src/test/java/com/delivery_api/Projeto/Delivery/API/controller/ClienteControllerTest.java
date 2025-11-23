package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.AuthenticationDTO;
import com.delivery_api.Projeto.Delivery.API.entity.Usuario;
import com.delivery_api.Projeto.Delivery.API.enums.Role;
import com.delivery_api.Projeto.Delivery.API.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        usuarioRepository.deleteAll();
        // Arrange: Create a user and log in to get a token
        String rawPassword = "password";
        Usuario user = new Usuario(null, "test@example.com", passwordEncoder.encode(rawPassword), "Test User", Role.CLIENTE, true, LocalDateTime.now(), null);
        usuarioRepository.save(user);
        AuthenticationDTO authDTO = new AuthenticationDTO("test@example.com", rawPassword);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        // Assuming the token is directly in the response body in a field named "token"
        token = objectMapper.readTree(response).get("token").asText();
    }

    @AfterEach
    void tearDown() {
        usuarioRepository.deleteAll();
    }

    @Test
    void listActiveClients_ShouldReturnUnauthorized_WhenNoTokenIsProvided() throws Exception {
        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listActiveClients_ShouldReturnOk_WhenTokenIsProvided() throws Exception {
        mockMvc.perform(get("/api/clientes")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
