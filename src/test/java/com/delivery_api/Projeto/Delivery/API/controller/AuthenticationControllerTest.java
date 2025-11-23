package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.AuthenticationDTO;
import com.delivery_api.Projeto.Delivery.API.dto.RegisterDTO;
import com.delivery_api.Projeto.Delivery.API.entity.Usuario;
import com.delivery_api.Projeto.Delivery.API.enums.Role;
import com.delivery_api.Projeto.Delivery.API.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void tearDown() {
        usuarioRepository.deleteAll();
    }

    @Test
    void register_ShouldReturnOk_WhenUserIsRegistered() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO("test@example.com", "password", "Test User", Role.CLIENTE, null);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk());

        Usuario savedUser = (Usuario) usuarioRepository.findByEmail("test@example.com");
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getNome()).isEqualTo("Test User");
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() throws Exception {
        // Arrange
        String rawPassword = "password";
        Usuario user = new Usuario(null, "login@example.com", passwordEncoder.encode(rawPassword), "Login User", Role.CLIENTE, true, LocalDateTime.now(), null);
        usuarioRepository.save(user);
        AuthenticationDTO authDTO = new AuthenticationDTO("login@example.com", rawPassword);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }
}
