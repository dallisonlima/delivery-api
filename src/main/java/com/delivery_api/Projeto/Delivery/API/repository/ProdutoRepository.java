package com.delivery_api.Projeto.Delivery.API.repository;

import com.delivery_api.Projeto.Delivery.API.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByRestauranteId(Long restauranteId);

    List<Produto> findByRestauranteIdAndDisponivelTrue(Long restauranteId); // Novo método

    List<Produto> findByDisponivelTrue();

    List<Produto> findByCategoria(String categoria);

    List<Produto> findByPrecoLessThanEqual(BigDecimal preco);

    List<Produto> findByNomeContainingIgnoreCase(String nome);
}
