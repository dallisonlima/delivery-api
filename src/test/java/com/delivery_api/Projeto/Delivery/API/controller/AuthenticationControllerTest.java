package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.AuthenticationDTO;
import com.delivery_api.Projeto.Delivery.API.dto.RegisterDTO;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @BeforeEach
    @AfterEach
    void tearDown() {
        usuarioRepository.deleteAll();
    }

    @Test
    void register_ShouldReturnCreated_WhenUserIsRegistered() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO("test@example.com", "password", "Test User", Role.CLIENTE, null);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("CLIENTE"));

        Usuario savedUser = (Usuario) usuarioRepository.findByEmail("test@example.com");
        assertThat(savedUser).isNotNull();
    }

    @Test
    void login_ShouldReturnLoginResponse_WhenCredentialsAreValid() throws Exception {
        String rawPassword = "password";
        Usuario user = new Usuario(1L, "login@example.com", passwordEncoder.encode(rawPassword), "Login User", Role.CLIENTE, true, LocalDateTime.now(), null);
        usuarioRepository.save(user);
        AuthenticationDTO authDTO = new AuthenticationDTO("login@example.com", rawPassword);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.expiration").isString())
                .andExpect(jsonPath("$.user.id").value(user.getId()))
                .andExpect(jsonPath("$.user.nome").value("Login User"));
    }

    @Test
    void login_ShouldReturnForbidden_WhenCredentialsAreInvalid() throws Exception {
        String rawPassword = "password";
        Usuario user = new Usuario(1L, "invalid@example.com", passwordEncoder.encode(rawPassword), "Invalid User", Role.CLIENTE, true, LocalDateTime.now(), null);
        usuarioRepository.save(user);
        AuthenticationDTO authDTO = new AuthenticationDTO("invalid@example.com", "wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void me_ShouldReturnUserData_WhenAuthenticated() throws Exception {
        String rawPassword = "password123";
        Usuario user = new Usuario(null, "me@example.com", passwordEncoder.encode(rawPassword), "Me User", Role.ADMIN, true, LocalDateTime.now(), null);
        usuarioRepository.save(user);
        AuthenticationDTO authDTO = new AuthenticationDTO("me@example.com", rawPassword);

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("token").asText();

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("Me User"))
                .andExpect(jsonPath("$.email").value("me@example.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }
}
