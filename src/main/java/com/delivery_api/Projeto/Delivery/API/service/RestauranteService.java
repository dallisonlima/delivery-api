package com.delivery_api.Projeto.Delivery.API.service;

import com.delivery_api.Projeto.Delivery.API.entity.Restaurante;
import com.delivery_api.Projeto.Delivery.API.repository.RestauranteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class RestauranteService {

    @Autowired
    private RestauranteRepository restauranteRepository;

    public Restaurante cadastrar(Restaurante restaurante) {
        validarRestaurante(restaurante);
        restaurante.setAtivo(true);
        return restauranteRepository.save(restaurante);
    }

    public Restaurante atualizar(Long id, Restaurante restauranteAtualizado) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado: " + id));

        validarRestaurante(restauranteAtualizado);

        restaurante.setNome(restauranteAtualizado.getNome());
        restaurante.setTaxaEntrega(restauranteAtualizado.getTaxaEntrega());
        restaurante.setCategoria(restauranteAtualizado.getCategoria());
        restaurante.setEndereco(restauranteAtualizado.getEndereco());
        restaurante.setAvaliacao(restauranteAtualizado.getAvaliacao());

        return restauranteRepository.save(restaurante);
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

    @Transactional(readOnly = true)
    public List<Restaurante> listarAtivos() {
        return restauranteRepository.findByAtivoTrue();
    }

    private void validarRestaurante(Restaurante restaurante) {
        if (!StringUtils.hasText(restaurante.getNome())) {
            throw new IllegalArgumentException("O nome do restaurante é obrigatório.");
        }
        if (restaurante.getTaxaEntrega() == null || restaurante.getTaxaEntrega().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("A taxa de entrega não pode ser negativa.");
        }
        if (!StringUtils.hasText(restaurante.getCategoria())) {
            throw new IllegalArgumentException("A categoria do restaurante é obrigatória.");
        }
    }
}
