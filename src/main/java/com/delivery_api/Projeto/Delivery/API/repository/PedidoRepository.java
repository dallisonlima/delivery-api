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

    @Query("SELECT p FROM Pedido p LEFT JOIN FETCH p.itens WHERE p.cliente.id = :clienteId")
    List<Pedido> findByClienteIdWithItens(@Param("clienteId") Long clienteId);

    List<Pedido> findByStatus(StatusPedido status);

    List<Pedido> findTop10ByOrderByDataPedidoDesc();

    List<Pedido> findByDataPedidoBetween(LocalDateTime inicio, LocalDateTime fim);

    @Query("SELECT p FROM Pedido p LEFT JOIN FETCH p.itens WHERE p.id = :pedidoId AND p.cliente.id = :clienteId")
    Optional<Pedido> findByIdAndClienteIdWithItens(Long pedidoId, Long clienteId);

    @Query("SELECT p.restaurante.nome, SUM(p.valorTotal) FROM Pedido p GROUP BY p.restaurante.nome")
    List<Object[]> findTotalVendasPorRestaurante();

    List<Pedido> findByValorTotalGreaterThan(BigDecimal valor);

    List<Pedido> findByDataPedidoBetweenAndStatus(LocalDateTime inicio, LocalDateTime fim, StatusPedido status);
}
