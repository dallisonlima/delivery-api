package com.delivery_api.Projeto.Delivery.API.repository;

import com.delivery_api.Projeto.Delivery.API.entity.Pedido;
import com.delivery_api.Projeto.Delivery.API.entity.StatusPedido;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalTime;

public class PedidoSpecs {

    public static Specification<Pedido> comStatus(StatusPedido status) {
        return (root, query, builder) ->
                builder.equal(root.get("status"), status);
    }

    public static Specification<Pedido> comData(LocalDate data) {
        return (root, query, builder) ->
                builder.between(root.get("dataPedido"), data.atStartOfDay(), data.atTime(LocalTime.MAX));
    }

    public static Specification<Pedido> doCliente(Long clienteId) {
        return (root, query, builder) ->
                builder.equal(root.get("cliente").get("id"), clienteId);
    }

    public static Specification<Pedido> doRestaurante(Long restauranteId) {
        return (root, query, builder) ->
                builder.equal(root.get("restaurante").get("id"), restauranteId);
    }
}
