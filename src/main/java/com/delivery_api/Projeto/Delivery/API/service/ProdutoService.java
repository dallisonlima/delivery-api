package com.delivery_api.Projeto.Delivery.API.service;

import com.delivery_api.Projeto.Delivery.API.entity.Produto;
import com.delivery_api.Projeto.Delivery.API.entity.Restaurante;
import com.delivery_api.Projeto.Delivery.API.repository.ProdutoRepository;
import com.delivery_api.Projeto.Delivery.API.repository.RestauranteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

@Service
@Transactional
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    public Produto cadastrar(Long restauranteId, Produto produto) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado: " + restauranteId));

        validarProduto(produto);
        produto.setRestaurante(restaurante);
        produto.setDisponivel(true);

        return produtoRepository.save(produto);
    }

    public void definirDisponibilidade(Long produtoId, boolean disponivel) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + produtoId));
        produto.setDisponivel(disponivel);
        produtoRepository.save(produto);
    }

    private void validarProduto(Produto produto) {
        if (!StringUtils.hasText(produto.getNome())) {
            throw new IllegalArgumentException("O nome do produto é obrigatório.");
        }
        if (produto.getPreco() == null || produto.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O preço do produto deve ser positivo.");
        }
    }
}
