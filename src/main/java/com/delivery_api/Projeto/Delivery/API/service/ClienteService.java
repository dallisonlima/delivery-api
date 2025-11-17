package com.delivery_api.Projeto.Delivery.API.service;

import com.delivery_api.Projeto.Delivery.API.dto.ClienteRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.ClienteResponseDTO;
import com.delivery_api.Projeto.Delivery.API.entity.Cliente;
import com.delivery_api.Projeto.Delivery.API.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public ClienteResponseDTO cadastrar(ClienteRequestDTO clienteDTO) {
        if (clienteRepository.existsByEmail(clienteDTO.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado: " + clienteDTO.getEmail());
        }

        Cliente cliente = new Cliente();
        cliente.setNome(clienteDTO.getNome());
        cliente.setEmail(clienteDTO.getEmail());
        cliente.setTelefone(clienteDTO.getTelefone());
        cliente.setEndereco(clienteDTO.getEndereco());
        cliente.setAtivo(true);

        Cliente clienteSalvo = clienteRepository.save(cliente);
        return toClienteResponseDTO(clienteSalvo);
    }

    @Transactional(readOnly = true)
    public Optional<ClienteResponseDTO> buscarPorId(Long id) {
        return clienteRepository.findById(id).map(this::toClienteResponseDTO);
    }

    @Transactional(readOnly = true)
    public Optional<ClienteResponseDTO> buscarPorEmail(String email) {
        return clienteRepository.findByEmail(email).map(this::toClienteResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listarAtivos() {
        return clienteRepository.findByAtivoTrue().stream()
                .map(this::toClienteResponseDTO)
                .collect(Collectors.toList());
    }

    public ClienteResponseDTO atualizar(Long id, ClienteRequestDTO clienteAtualizadoDTO) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));

        if (!cliente.getEmail().equals(clienteAtualizadoDTO.getEmail()) &&
                clienteRepository.existsByEmail(clienteAtualizadoDTO.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado: " + clienteAtualizadoDTO.getEmail());
        }

        cliente.setNome(clienteAtualizadoDTO.getNome());
        cliente.setEmail(clienteAtualizadoDTO.getEmail());
        cliente.setTelefone(clienteAtualizadoDTO.getTelefone());
        cliente.setEndereco(clienteAtualizadoDTO.getEndereco());

        Cliente clienteSalvo = clienteRepository.save(cliente);
        return toClienteResponseDTO(clienteSalvo);
    }

    public void ativarDesativarCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));
        cliente.setAtivo(!cliente.getAtivo()); // Alterna o status
        clienteRepository.save(cliente);
    }

    @Transactional(readOnly = true)
    public List<Cliente> buscarPorNome(String nome) {
        // Este método ainda retorna Cliente, se precisar de DTO, me avise.
        return clienteRepository.findByNomeContainingIgnoreCase(nome);
    }

    private ClienteResponseDTO toClienteResponseDTO(Cliente cliente) {
        ClienteResponseDTO dto = new ClienteResponseDTO();
        dto.setId(cliente.getId());
        dto.setNome(cliente.getNome());
        dto.setEmail(cliente.getEmail());
        dto.setTelefone(cliente.getTelefone());
        dto.setEndereco(cliente.getEndereco());
        dto.setAtivo(cliente.getAtivo());
        return dto;
    }
}
