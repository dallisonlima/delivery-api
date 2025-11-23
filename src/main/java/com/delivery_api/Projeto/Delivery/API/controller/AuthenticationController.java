package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.AuthenticationDTO;
import com.delivery_api.Projeto.Delivery.API.dto.LoginResponseDTO;
import com.delivery_api.Projeto.Delivery.API.dto.RegisterDTO;
import com.delivery_api.Projeto.Delivery.API.dto.UserResponseDTO;
import com.delivery_api.Projeto.Delivery.API.entity.Usuario;
import com.delivery_api.Projeto.Delivery.API.repository.UsuarioRepository;
import com.delivery_api.Projeto.Delivery.API.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Date;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.senha());
        Authentication auth = this.authenticationManager.authenticate(usernamePassword);

        Usuario usuario = (Usuario) auth.getPrincipal();
        String token = tokenService.generateToken(usuario);
        Date expiration = tokenService.extractExpiration(token);
        UserResponseDTO userResponse = new UserResponseDTO(usuario);

        return ResponseEntity.ok(new LoginResponseDTO(token, expiration, userResponse));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody @Valid RegisterDTO data) {
        if (this.usuarioRepository.findByEmail(data.email()) != null) {
            return ResponseEntity.badRequest().build();
        }

        String encryptedPassword = passwordEncoder.encode(data.senha());
        Usuario newUser = new Usuario(null, data.email(), encryptedPassword, data.nome(), data.role(), true, LocalDateTime.now(), data.restauranteId());

        Usuario savedUser = this.usuarioRepository.save(newUser);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/auth/me")
                .build().toUri();

        return ResponseEntity.created(location).body(new UserResponseDTO(savedUser));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> me() {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(new UserResponseDTO(usuario));
    }
}
