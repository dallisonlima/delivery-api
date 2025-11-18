package com.delivery_api.Projeto.Delivery.API.service;

import com.delivery_api.Projeto.Delivery.API.dto.ClienteRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.ClienteResponseDTO;
import com.delivery_api.Projeto.Delivery.API.entity.Cliente;
import com.delivery_api.Projeto.Delivery.API.repository.ClienteRepository;
import com.delivery_api.Projeto.Delivery.API.exception.EntityNotFoundException;
import com.delivery_api.Projeto.Delivery.API.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public ClienteResponseDTO cadastrar(ClienteRequestDTO clienteDTO) {
        if (clienteRepository.existsByEmail(clienteDTO.getEmail())) {
            throw new BusinessException("Email já cadastrado: " + clienteDTO.getEmail());
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
    public ClienteResponseDTO buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .map(this::toClienteResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarPorEmail(String email) {
        return clienteRepository.findByEmail(email)
                .map(this::toClienteResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com o email: " + email));
    }

    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listarAtivos() {
        return clienteRepository.findByAtivoTrue().stream()
                .map(this::toClienteResponseDTO)
                .collect(Collectors.toList());
    }

    public ClienteResponseDTO atualizar(Long id, ClienteRequestDTO clienteAtualizadoDTO) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado: " + id));

        if (!cliente.getEmail().equals(clienteAtualizadoDTO.getEmail()) &&
                clienteRepository.existsByEmail(clienteAtualizadoDTO.getEmail())) {
            throw new BusinessException("Email já cadastrado: " + clienteAtualizadoDTO.getEmail());
        }

        cliente.setNome(clienteAtualizadoDTO.getNome());
        cliente.setEmail(clienteAtualizadoDTO.getEmail());
        cliente.setTelefone(clienteAtualizadoDTO.getTelefone());
        cliente.setEndereco(clienteAtualizadoDTO.getEndereco());

        Cliente clienteSalvo = clienteRepository.save(cliente);
        return toClienteResponseDTO(clienteSalvo);
    }

    public ClienteResponseDTO ativarDesativarCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado: " + id));
        cliente.setAtivo(!cliente.getAtivo());
        Cliente clienteSalvo = clienteRepository.save(cliente);
        return toClienteResponseDTO(clienteSalvo);
    }

    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> buscarPorNome(String nome) {
        return clienteRepository.findByNomeContainingIgnoreCase(nome).stream()
                .map(this::toClienteResponseDTO)
                .collect(Collectors.toList());
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
