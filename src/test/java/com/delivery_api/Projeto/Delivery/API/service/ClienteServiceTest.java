package com.delivery_api.Projeto.Delivery.API.service;

import com.delivery_api.Projeto.Delivery.API.dto.ClienteRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.ClienteResponseDTO;
import com.delivery_api.Projeto.Delivery.API.entity.Cliente;
import com.delivery_api.Projeto.Delivery.API.exception.ConflictException;
import com.delivery_api.Projeto.Delivery.API.exception.EntityNotFoundException;
import com.delivery_api.Projeto.Delivery.API.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    private ClienteRequestDTO clienteRequestDTO;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        clienteRequestDTO = new ClienteRequestDTO();
        clienteRequestDTO.setNome("João Silva");
        clienteRequestDTO.setEmail("joao@example.com");
        clienteRequestDTO.setTelefone("11999998888");

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setEmail("joao@example.com");
    }

    @Test
    void cadastrar_ComDadosValidos_DeveRetornarClienteResponseDTO() {
        when(clienteRepository.existsByEmail(any(String.class))).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        ClienteResponseDTO response = clienteService.cadastrar(clienteRequestDTO);

        assertThat(response).isNotNull();
        assertThat(response.getNome()).isEqualTo("João Silva");
    }

    @Test
    void cadastrar_ComEmailDuplicado_DeveLancarConflictException() {
        when(clienteRepository.existsByEmail("joao@example.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> {
            clienteService.cadastrar(clienteRequestDTO);
        });
    }

    @Test
    void buscarPorId_QuandoClienteExiste_DeveRetornarClienteResponseDTO() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        ClienteResponseDTO response = clienteService.buscarPorId(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    void buscarPorId_QuandoClienteNaoExiste_DeveLancarEntityNotFoundException() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            clienteService.buscarPorId(99L);
        });
    }

    @Test
    void listarAtivos_DeveRetornarPaginaDeClientes() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cliente> clientePage = new PageImpl<>(Collections.singletonList(cliente), pageable, 1);

        when(clienteRepository.findByAtivoTrue(pageable)).thenReturn(clientePage);

        Page<ClienteResponseDTO> responsePage = clienteService.listarAtivos(pageable);

        assertThat(responsePage).isNotNull();
        assertThat(responsePage.getContent()).hasSize(1);
        assertThat(responsePage.getContent().get(0).getNome()).isEqualTo("João Silva");
    }
}
