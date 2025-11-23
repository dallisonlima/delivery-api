package com.delivery_api.Projeto.Delivery.API.service;

import com.delivery_api.Projeto.Delivery.API.dto.request.ClienteRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.response.ClienteResponseDTO;
import com.delivery_api.Projeto.Delivery.API.dto.EnderecoDTO;
import com.delivery_api.Projeto.Delivery.API.entity.Cliente;
import com.delivery_api.Projeto.Delivery.API.entity.Endereco;
import com.delivery_api.Projeto.Delivery.API.repository.ClienteRepository;
import com.delivery_api.Projeto.Delivery.API.exception.EntityNotFoundException;
import com.delivery_api.Projeto.Delivery.API.exception.ConflictException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public ClienteResponseDTO cadastrar(ClienteRequestDTO clienteDTO) {
        if (clienteRepository.existsByEmail(clienteDTO.getEmail())) {
            throw new ConflictException("Email já cadastrado: " + clienteDTO.getEmail());
        }

        Cliente cliente = new Cliente();
        cliente.setNome(clienteDTO.getNome());
        cliente.setEmail(clienteDTO.getEmail());
        cliente.setTelefone(clienteDTO.getTelefone());
        cliente.setAtivo(true);

        Endereco endereco = new Endereco();
        endereco.setCep(clienteDTO.getCep());
        endereco.setLogradouro(clienteDTO.getLogradouro());
        endereco.setNumero(clienteDTO.getNumero());
        endereco.setComplemento(clienteDTO.getComplemento());
        endereco.setBairro(clienteDTO.getBairro());
        endereco.setCidade(clienteDTO.getCidade());
        endereco.setEstado(clienteDTO.getEstado());
        cliente.setEndereco(endereco);

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
    public Page<ClienteResponseDTO> listarAtivos(Pageable pageable) {
        return clienteRepository.findByAtivoTrue(pageable).map(this::toClienteResponseDTO);
    }

    public ClienteResponseDTO atualizar(Long id, ClienteRequestDTO clienteDTO) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado: " + id));

        if (!cliente.getEmail().equals(clienteDTO.getEmail()) &&
                clienteRepository.existsByEmail(clienteDTO.getEmail())) {
            throw new ConflictException("Email já cadastrado: " + clienteDTO.getEmail());
        }

        cliente.setNome(clienteDTO.getNome());
        cliente.setEmail(clienteDTO.getEmail());
        cliente.setTelefone(clienteDTO.getTelefone());

        Endereco endereco = cliente.getEndereco() != null ? cliente.getEndereco() : new Endereco();
        endereco.setCep(clienteDTO.getCep());
        endereco.setLogradouro(clienteDTO.getLogradouro());
        endereco.setNumero(clienteDTO.getNumero());
        endereco.setComplemento(clienteDTO.getComplemento());
        endereco.setBairro(clienteDTO.getBairro());
        endereco.setCidade(clienteDTO.getCidade());
        endereco.setEstado(clienteDTO.getEstado());
        cliente.setEndereco(endereco);

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
    public Page<ClienteResponseDTO> buscarPorNome(String nome, Pageable pageable) {
        return clienteRepository.findByNomeContainingIgnoreCase(nome, pageable)
                .map(this::toClienteResponseDTO);
    }

    private ClienteResponseDTO toClienteResponseDTO(Cliente cliente) {
        ClienteResponseDTO dto = new ClienteResponseDTO();
        dto.setId(cliente.getId());
        dto.setNome(cliente.getNome());
        dto.setEmail(cliente.getEmail());
        dto.setTelefone(cliente.getTelefone());
        dto.setAtivo(cliente.getAtivo());

        if (cliente.getEndereco() != null) {
            EnderecoDTO enderecoDTO = new EnderecoDTO();
            enderecoDTO.setCep(cliente.getEndereco().getCep());
            enderecoDTO.setLogradouro(cliente.getEndereco().getLogradouro());
            enderecoDTO.setNumero(cliente.getEndereco().getNumero());
            enderecoDTO.setComplemento(cliente.getEndereco().getComplemento());
            enderecoDTO.setBairro(cliente.getEndereco().getBairro());
            enderecoDTO.setCidade(cliente.getEndereco().getCidade());
            enderecoDTO.setEstado(cliente.getEndereco().getEstado());
            dto.setEndereco(enderecoDTO);
        }

        return dto;
    }
}
