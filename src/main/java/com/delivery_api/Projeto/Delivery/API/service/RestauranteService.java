package com.delivery_api.Projeto.Delivery.API.service;

import com.delivery_api.Projeto.Delivery.API.dto.RestauranteRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.RestauranteResponseDTO;
import com.delivery_api.Projeto.Delivery.API.entity.Restaurante;
import com.delivery_api.Projeto.Delivery.API.repository.RestauranteRepository;
import com.delivery_api.Projeto.Delivery.API.repository.RestauranteSpecs;
import com.delivery_api.Projeto.Delivery.API.exception.EntityNotFoundException;
import com.delivery_api.Projeto.Delivery.API.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

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
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado: " + id));

        restaurante.setNome(restauranteDTO.getNome());
        restaurante.setTaxaEntrega(restauranteDTO.getTaxaEntrega());
        restaurante.setCategoria(restauranteDTO.getCategoria());
        restaurante.setEndereco(restauranteDTO.getEndereco());
        restaurante.setTelefone(restauranteDTO.getTelefone());

        Restaurante restauranteSalvo = restauranteRepository.save(restaurante);
        return toRestauranteResponseDTO(restauranteSalvo);
    }

    public RestauranteResponseDTO ativarOuDesativar(Long id, boolean ativo) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado: " + id));
        restaurante.setAtivo(ativo);
        Restaurante restauranteSalvo = restauranteRepository.save(restaurante);
        return toRestauranteResponseDTO(restauranteSalvo);
    }

    public void deletar(Long id) {
        if (!restauranteRepository.existsById(id)) {
            throw new EntityNotFoundException("Restaurante não encontrado: " + id);
        }
        try {
            restauranteRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Não é possível deletar o restaurante pois ele possui pedidos associados. Considere inativá-lo.");
        }
    }

    @Transactional(readOnly = true)
    public Page<RestauranteResponseDTO> listar(String categoria, Boolean ativo, Pageable pageable) {
        Specification<Restaurante> spec = Specification.where(null);
        if (StringUtils.hasText(categoria)) {
            spec = spec.and(RestauranteSpecs.comCategoria(categoria));
        }
        if (ativo != null) {
            spec = spec.and(RestauranteSpecs.comAtivo(ativo));
        }
        return restauranteRepository.findAll(spec, pageable).map(this::toRestauranteResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<RestauranteResponseDTO> buscarRestaurantesDisponiveis(Pageable pageable) {
        return restauranteRepository.findByAtivoTrue(pageable).map(this::toRestauranteResponseDTO);
    }

    @Transactional(readOnly = true)
    public RestauranteResponseDTO buscarPorId(Long id) {
        return restauranteRepository.findById(id)
                .map(this::toRestauranteResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado: " + id));
    }
    
    @Transactional(readOnly = true)
    public Page<RestauranteResponseDTO> buscarPorCategoria(String categoria, Pageable pageable) {
        return restauranteRepository.findByCategoria(categoria, pageable).map(this::toRestauranteResponseDTO);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularTaxaEntrega(Long restauranteId, String cep) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado: " + restauranteId));
        // Lógica de cálculo de CEP pode ser adicionada aqui no futuro
        return restaurante.getTaxaEntrega();
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
