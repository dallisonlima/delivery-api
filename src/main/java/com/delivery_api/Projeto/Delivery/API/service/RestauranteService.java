package com.delivery_api.Projeto.Delivery.API.service;

import com.delivery_api.Projeto.Delivery.API.dto.EnderecoDTO;
import com.delivery_api.Projeto.Delivery.API.dto.request.RestauranteRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.response.RestauranteResponseDTO;
import com.delivery_api.Projeto.Delivery.API.entity.Endereco;
import com.delivery_api.Projeto.Delivery.API.entity.Restaurante;
import com.delivery_api.Projeto.Delivery.API.entity.Usuario;
import com.delivery_api.Projeto.Delivery.API.repository.RestauranteRepository;
import com.delivery_api.Projeto.Delivery.API.repository.RestauranteSpecs;
import com.delivery_api.Projeto.Delivery.API.exception.EntityNotFoundException;
import com.delivery_api.Projeto.Delivery.API.exception.ConflictException;
import com.delivery_api.Projeto.Delivery.API.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    @CacheEvict(value = "restaurantes", allEntries = true)
    public RestauranteResponseDTO cadastrar(RestauranteRequestDTO restauranteDTO) {
        Restaurante restaurante = new Restaurante();
        restaurante.setNome(restauranteDTO.getNome());
        restaurante.setTaxaEntrega(restauranteDTO.getTaxaEntrega());
        restaurante.setTempoDeEntrega(restauranteDTO.getTempoDeEntrega());
        restaurante.setHorarioFuncionamento(restauranteDTO.getHorarioFuncionamento());
        restaurante.setCategoria(restauranteDTO.getCategoria());
        restaurante.setTelefone(restauranteDTO.getTelefone());
        restaurante.setAtivo(true);

        Endereco endereco = new Endereco();
        endereco.setCep(restauranteDTO.getCep());
        endereco.setLogradouro(restauranteDTO.getLogradouro());
        endereco.setNumero(restauranteDTO.getNumero());
        endereco.setComplemento(restauranteDTO.getComplemento());
        endereco.setBairro(restauranteDTO.getBairro());
        endereco.setCidade(restauranteDTO.getCidade());
        endereco.setEstado(restauranteDTO.getEstado());
        restaurante.setEndereco(endereco);

        Restaurante restauranteSalvo = restauranteRepository.save(restaurante);
        return toRestauranteResponseDTO(restauranteSalvo);
    }

    @CacheEvict(value = "restaurantes", allEntries = true)
    public RestauranteResponseDTO atualizar(Long id, RestauranteRequestDTO restauranteDTO) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado: " + id));

        restaurante.setNome(restauranteDTO.getNome());
        restaurante.setTaxaEntrega(restauranteDTO.getTaxaEntrega());
        restaurante.setTempoDeEntrega(restauranteDTO.getTempoDeEntrega());
        restaurante.setHorarioFuncionamento(restauranteDTO.getHorarioFuncionamento());
        restaurante.setCategoria(restauranteDTO.getCategoria());
        restaurante.setTelefone(restauranteDTO.getTelefone());

        Endereco endereco = restaurante.getEndereco() != null ? restaurante.getEndereco() : new Endereco();
        endereco.setCep(restauranteDTO.getCep());
        endereco.setLogradouro(restauranteDTO.getLogradouro());
        endereco.setNumero(restauranteDTO.getNumero());
        endereco.setComplemento(restauranteDTO.getComplemento());
        endereco.setBairro(restauranteDTO.getBairro());
        endereco.setCidade(restauranteDTO.getCidade());
        endereco.setEstado(restauranteDTO.getEstado());
        restaurante.setEndereco(endereco);

        Restaurante restauranteSalvo = restauranteRepository.save(restaurante);
        return toRestauranteResponseDTO(restauranteSalvo);
    }

    @CacheEvict(value = "restaurantes", allEntries = true)
    public RestauranteResponseDTO ativarOuDesativar(Long id, boolean ativo) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado: " + id));
        restaurante.setAtivo(ativo);
        Restaurante restauranteSalvo = restauranteRepository.save(restaurante);
        return toRestauranteResponseDTO(restauranteSalvo);
    }

    @CacheEvict(value = "restaurantes", allEntries = true)
    public void deletar(Long id) {
        if (!restauranteRepository.existsById(id)) {
            throw new EntityNotFoundException("Restaurante não encontrado: " + id);
        }
        try {
            restauranteRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Não é possível deletar o restaurante pois ele possui pedidos associados. Considere inativá-lo.");
        }
    }

    @Cacheable("restaurantes")
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

    @Cacheable("restaurantes")
    @Transactional(readOnly = true)
    public Page<RestauranteResponseDTO> buscarRestaurantesDisponiveis(Pageable pageable) {
        return restauranteRepository.findByAtivoTrue(pageable).map(this::toRestauranteResponseDTO);
    }

    @Cacheable(value = "restaurantes", key = "#id")
    @Transactional(readOnly = true)
    public RestauranteResponseDTO buscarPorId(Long id) {
        return restauranteRepository.findById(id)
                .map(this::toRestauranteResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado: " + id));
    }
    
    @Cacheable("restaurantes")
    @Transactional(readOnly = true)
    public Page<RestauranteResponseDTO> buscarPorCategoria(String categoria, Pageable pageable) {
        return restauranteRepository.findByCategoria(categoria, pageable).map(this::toRestauranteResponseDTO);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularTaxaEntrega(Long restauranteId, String cep) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado: " + restauranteId));
        return restaurante.getTaxaEntrega();
    }

    public boolean isOwner(Long restauranteId) {
        Usuario currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null || currentUser.getRestauranteId() == null) {
            return false;
        }
        return currentUser.getRestauranteId().equals(restauranteId);
    }

    private RestauranteResponseDTO toRestauranteResponseDTO(Restaurante restaurante) {
        RestauranteResponseDTO dto = new RestauranteResponseDTO();
        dto.setId(restaurante.getId());
        dto.setNome(restaurante.getNome());
        dto.setTaxaEntrega(restaurante.getTaxaEntrega());
        dto.setTempoDeEntrega(restaurante.getTempoDeEntrega());
        dto.setHorarioFuncionamento(restaurante.getHorarioFuncionamento());
        dto.setCategoria(restaurante.getCategoria());
        dto.setAtivo(restaurante.getAtivo());
        dto.setAvaliacao(restaurante.getAvaliacao());
        dto.setTelefone(restaurante.getTelefone());

        if (restaurante.getEndereco() != null) {
            EnderecoDTO enderecoDTO = new EnderecoDTO();
            enderecoDTO.setCep(restaurante.getEndereco().getCep());
            enderecoDTO.setLogradouro(restaurante.getEndereco().getLogradouro());
            enderecoDTO.setNumero(restaurante.getEndereco().getNumero());
            enderecoDTO.setComplemento(restaurante.getEndereco().getComplemento());
            enderecoDTO.setBairro(restaurante.getEndereco().getBairro());
            enderecoDTO.setCidade(restaurante.getEndereco().getCidade());
            enderecoDTO.setEstado(restaurante.getEndereco().getEstado());
            dto.setEndereco(enderecoDTO);
        }
        
        return dto;
    }
}
