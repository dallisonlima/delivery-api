package com.delivery_api.Projeto.Delivery.API.repository;

import com.delivery_api.Projeto.Delivery.API.entity.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByRestauranteId(Long restauranteId);

    Page<Produto> findByRestauranteIdAndDisponivelTrue(Long restauranteId, Pageable pageable);

    List<Produto> findByDisponivelTrue();

    Page<Produto> findByCategoria(String categoria, Pageable pageable);

    List<Produto> findByPrecoLessThanEqual(BigDecimal preco);

    Page<Produto> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    Collection<Object> findByCategoria(String japonesa);
}
