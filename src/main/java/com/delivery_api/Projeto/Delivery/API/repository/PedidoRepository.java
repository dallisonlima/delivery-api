package com.delivery_api.Projeto.Delivery.API.repository;

import com.delivery_api.Projeto.Delivery.API.entity.Pedido;
import com.delivery_api.Projeto.Delivery.API.entity.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByClienteId(Long clienteId);

    List<Pedido> findByStatus(StatusPedido status);

    List<Pedido> findTop10ByOrderByDataPedidoDesc();

    List<Pedido> findByDataPedidoBetween(LocalDateTime inicio, LocalDateTime fim);

    @Query("SELECT p FROM Pedido p LEFT JOIN FETCH p.itens WHERE p.id = :pedidoId AND p.cliente.id = :clienteId")
    Optional<Pedido> findByIdAndClienteIdWithItens(Long pedidoId, Long clienteId);

    @Query("SELECT p.restaurante.nome, SUM(p.valorTotal) FROM Pedido p GROUP BY p.restaurante.nome")
    List<Object[]> findTotalVendasPorRestaurante();

    @Query("SELECT p FROM Pedido p WHERE p.valorTotal > :valor")
    List<Pedido> findByValorTotalGreaterThan(@Param("valor") BigDecimal valor);

    @Query("SELECT p FROM Pedido p WHERE p.dataPedido BETWEEN :inicio AND :fim AND p.status = :status")
    List<Pedido> findByDataPedidoBetweenAndStatus(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim, @Param("status") StatusPedido status);
}
