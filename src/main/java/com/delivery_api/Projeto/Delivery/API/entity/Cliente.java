package com.delivery_api.Projeto.Delivery.API.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String email;

    private String telefone;

    @Embedded
    private Endereco endereco;

    private Boolean ativo;

    // Construtor de conveniência para testes
    public Cliente(String nome, String email, String telefone, String endereco, boolean ativo) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        // Este construtor pode precisar de ajuste ou ser removido se o endereço for complexo
        this.ativo = ativo;
    }
}
