package com.delivery_api.Projeto.Delivery.API.service;

import com.delivery_api.Projeto.Delivery.API.entity.Usuario;
import com.delivery_api.Projeto.Delivery.API.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TokenServiceTest {

    private TokenService tokenService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "jwtSecret", "my-super-secret-key-for-jwt-token-generation-for-testing-purpose");

        usuario = new Usuario(1L, "test@example.com", "password", "Test User", Role.RESTAURANTE, true, LocalDateTime.now(), 10L);
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        String token = tokenService.generateToken(usuario);

        assertThat(token).isNotNull().isNotEmpty();
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        String token = tokenService.generateToken(usuario);
        String username = tokenService.extractUsername(token);

        assertThat(username).isEqualTo("test@example.com");
    }

    @Test
    void validateToken_ShouldReturnTrue_ForValidToken() {
        String token = tokenService.generateToken(usuario);
        boolean isValid = tokenService.validateToken(token, usuario);

        assertThat(isValid).isTrue();
    }

    @Test
    void validateToken_ShouldReturnFalse_ForInvalidUsername() {
        String token = tokenService.generateToken(usuario);
        Usuario otherUser = new Usuario(2L, "other@example.com", "password", "Other User", Role.CLIENTE, true, LocalDateTime.now(), null);
        boolean isValid = tokenService.validateToken(token, otherUser);

        assertThat(isValid).isFalse();
    }

    @Test
    void generateToken_ShouldContainCustomClaims() {
        String token = tokenService.generateToken(usuario);

        Long userId = tokenService.extractClaim(token, claims -> claims.get("userId", Long.class));
        String role = tokenService.extractClaim(token, claims -> claims.get("role", String.class));
        Long restauranteId = tokenService.extractClaim(token, claims -> claims.get("restauranteId", Long.class));

        assertThat(userId).isEqualTo(1L);
        assertThat(role).isEqualTo(Role.RESTAURANTE.name());
        assertThat(restauranteId).isEqualTo(10L);
    }
}
