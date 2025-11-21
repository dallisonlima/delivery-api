package com.delivery_api.Projeto.Delivery.API.repository;

import com.delivery_api.Projeto.Delivery.API.entity.Restaurante;
import org.springframework.data.jpa.domain.Specification;

public class RestauranteSpecs {

    public static Specification<Restaurante> comCategoria(String categoria) {
        return (root, query, builder) ->
                builder.equal(root.get("categoria"), categoria);
    }

    public static Specification<Restaurante> comAtivo(Boolean ativo) {
        return (root, query, builder) ->
                builder.equal(root.get("ativo"), ativo);
    }
}
