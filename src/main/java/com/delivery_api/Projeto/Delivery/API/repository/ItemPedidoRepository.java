package com.delivery_api.Projeto.Delivery.API.repository;

import com.delivery_api.Projeto.Delivery.API.entity.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {

    @Query("SELECT ip.produto.nome, SUM(ip.quantidade) as totalVendido " +
           "FROM ItemPedido ip " +
           "GROUP BY ip.produto.nome " +
           "ORDER BY totalVendido DESC")
    List<Object[]> findProdutosMaisVendidos();
}
