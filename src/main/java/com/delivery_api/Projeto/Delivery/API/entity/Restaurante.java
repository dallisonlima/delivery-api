package com.delivery_api.Projeto.Delivery.API.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "restaurantes")
public class Restaurante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(name = "taxa_entrega")
    private BigDecimal taxaEntrega;

    @Column(name = "tempo_entrega")
    private Integer tempoDeEntrega;

    @Column(name = "horario_funcionamento")
    private String horarioFuncionamento;

    private String categoria;

    private Boolean ativo;

    @Embedded
    private Endereco endereco;

    private Double avaliacao;

    private String telefone;

    public Restaurante(String nome, BigDecimal taxaEntrega, String categoria, boolean ativo, String endereco, String telefone, double avaliacao) {
        this.nome = nome;
        this.taxaEntrega = taxaEntrega;
        this.categoria = categoria;
        this.ativo = ativo;
        // Este construtor pode precisar de ajuste ou ser removido se o endereço for complexo
    }
}
