package com.delivery_api.Projeto.Delivery.API.service;

import com.delivery_api.Projeto.Delivery.API.dto.RestauranteRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.RestauranteResponseDTO;
import com.delivery_api.Projeto.Delivery.API.entity.Restaurante;
import com.delivery_api.Projeto.Delivery.API.repository.RestauranteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class RestauranteService {

    @Autowired
    private RestauranteRepository restauranteRepository;

    public RestauranteResponseDTO cadastrar(RestauranteRequestDTO restauranteDTO) {
        Restaurante restaurante = new Restaurante();
        restaurante.setNome(restauranteDTO.getNome());
        restaurante.setTaxaEntrega(restauranteDTO.getTaxaEntrega());
        restaurante.setCategoria(restauranteDTO.getCategoria());
        restaurante.setEndereco(restauranteDTO.getEndereco());
        restaurante.setTelefone(restauranteDTO.getTelefone());
        restaurante.setAtivo(true);

        Restaurante restauranteSalvo = restauranteRepository.save(restaurante);
        return toRestauranteResponseDTO(restauranteSalvo);
    }

    public RestauranteResponseDTO atualizar(Long id, RestauranteRequestDTO restauranteDTO) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado: " + id));

        restaurante.setNome(restauranteDTO.getNome());
        restaurante.setTaxaEntrega(restauranteDTO.getTaxaEntrega());
        restaurante.setCategoria(restauranteDTO.getCategoria());
        restaurante.setEndereco(restauranteDTO.getEndereco());
        restaurante.setTelefone(restauranteDTO.getTelefone());

        Restaurante restauranteSalvo = restauranteRepository.save(restaurante);
        return toRestauranteResponseDTO(restauranteSalvo);
    }

    public void ativar(Long id) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado: " + id));
        restaurante.setAtivo(true);
        restauranteRepository.save(restaurante);
    }

    public void inativar(Long id) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado: " + id));
        restaurante.setAtivo(false);
        restauranteRepository.save(restaurante);
    }

    public void deletar(Long id) {
        if (!restauranteRepository.existsById(id)) {
            throw new IllegalArgumentException("Restaurante não encontrado: " + id);
        }
        restauranteRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<RestauranteResponseDTO> listarTodos() {
        return restauranteRepository.findAll().stream()
                .map(this::toRestauranteResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<RestauranteResponseDTO> buscarPorId(Long id) {
        return restauranteRepository.findById(id).map(this::toRestauranteResponseDTO);
    }
    
    @Transactional(readOnly = true)
    public List<RestauranteResponseDTO> buscarPorCategoria(String categoria) {
        return restauranteRepository.findByCategoria(categoria).stream()
                .map(this::toRestauranteResponseDTO)
                .collect(Collectors.toList());
    }

    private RestauranteResponseDTO toRestauranteResponseDTO(Restaurante restaurante) {
        RestauranteResponseDTO dto = new RestauranteResponseDTO();
        dto.setId(restaurante.getId());
        dto.setNome(restaurante.getNome());
        dto.setTaxaEntrega(restaurante.getTaxaEntrega());
        dto.setCategoria(restaurante.getCategoria());
        dto.setAtivo(restaurante.getAtivo());
        dto.setEndereco(restaurante.getEndereco());
        dto.setAvaliacao(restaurante.getAvaliacao());
        dto.setTelefone(restaurante.getTelefone());
        return dto;
    }
}
