package com.delivery_api.Projeto.Delivery.API.config;

import com.delivery_api.Projeto.Delivery.API.entity.*;
import com.delivery_api.Projeto.Delivery.API.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private RestauranteRepository restauranteRepository;
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private PedidoRepository pedidoRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Endereços
        Endereco end1 = new Endereco();
        end1.setCep("11111-111");
        end1.setLogradouro("Rua A");
        end1.setNumero("123");
        end1.setCidade("São Paulo");
        end1.setEstado("SP");

        Endereco end2 = new Endereco();
        end2.setCep("22222-222");
        end2.setLogradouro("Rua B");
        end2.setNumero("456");
        end2.setCidade("Rio de Janeiro");
        end2.setEstado("RJ");

        Endereco end3 = new Endereco();
        end3.setCep("33333-333");
        end3.setLogradouro("Av. Principal");
        end3.setNumero("1");
        end3.setCidade("São Paulo");
        end3.setEstado("SP");

        Endereco end4 = new Endereco();
        end4.setCep("44444-444");
        end4.setLogradouro("Av. Secundária");
        end4.setNumero("2");
        end4.setCidade("São Paulo");
        end4.setEstado("SP");

        // Clientes
        Cliente cliente1 = new Cliente();
        cliente1.setNome("Carlos Souza");
        cliente1.setEmail("carlos@example.com");
        cliente1.setTelefone("11987654321");
        cliente1.setEndereco(end1);
        cliente1.setAtivo(true);

        Cliente cliente2 = new Cliente();
        cliente2.setNome("Ana Pereira");
        cliente2.setEmail("ana@example.com");
        cliente2.setTelefone("21912345678");
        cliente2.setEndereco(end2);
        cliente2.setAtivo(true);
        
        clienteRepository.saveAll(Arrays.asList(cliente1, cliente2));

        // Restaurantes
        Restaurante rest1 = new Restaurante();
        rest1.setNome("Pizzaria Delícia");
        rest1.setTaxaEntrega(new BigDecimal("5.00"));
        rest1.setTempoDeEntrega(40);
        rest1.setHorarioFuncionamento("18:00-23:00");
        rest1.setCategoria("Pizza");
        rest1.setAtivo(true);
        rest1.setTelefone("1122223333");
        rest1.setEndereco(end3);

        Restaurante rest2 = new Restaurante();
        rest2.setNome("Sushi House");
        rest2.setTaxaEntrega(new BigDecimal("10.00"));
        rest2.setTempoDeEntrega(50);
        rest2.setHorarioFuncionamento("19:00-22:30");
        rest2.setCategoria("Japonesa");
        rest2.setAtivo(true);
        rest2.setTelefone("1144445555");
        rest2.setEndereco(end4);

        restauranteRepository.saveAll(Arrays.asList(rest1, rest2));

        // Produtos
        Produto prod1 = new Produto();
        prod1.setNome("Pizza Margherita");
        prod1.setDescricao("Molho, muçarela e manjericão");
        prod1.setPreco(new BigDecimal("45.00"));
        prod1.setCategoria("Pizza");
        prod1.setDisponivel(true);
        prod1.setRestaurante(rest1);

        Produto prod2 = new Produto();
        prod2.setNome("Pizza Calabresa");
        prod2.setDescricao("Muçarela e calabresa");
        prod2.setPreco(new BigDecimal("50.00"));
        prod2.setCategoria("Pizza");
        prod2.setDisponivel(true);
        prod2.setRestaurante(rest1);

        Produto prod3 = new Produto();
        prod3.setNome("Combinado Salmão");
        prod3.setDescricao("20 peças de salmão");
        prod3.setPreco(new BigDecimal("80.00"));
        prod3.setCategoria("Japonesa");
        prod3.setDisponivel(true);
        prod3.setRestaurante(rest2);

        Produto prod4 = new Produto();
        prod4.setNome("Temaki Filadélfia");
        prod4.setDescricao("Salmão, cream cheese e cebolinha");
        prod4.setPreco(new BigDecimal("25.00"));
        prod4.setCategoria("Japonesa");
        prod4.setDisponivel(true);
        prod4.setRestaurante(rest2);

        Produto prod5 = new Produto();
        prod5.setNome("Refrigerante Lata");
        prod5.setDescricao("Coca-Cola");
        prod5.setPreco(new BigDecimal("6.00"));
        prod5.setCategoria("Bebida");
        prod5.setDisponivel(true);
        prod5.setRestaurante(rest1);
        
        produtoRepository.saveAll(Arrays.asList(prod1, prod2, prod3, prod4, prod5));

        // Pedidos
        Pedido pedido1 = new Pedido();
        pedido1.setCliente(cliente1);
        pedido1.setRestaurante(rest1);
        pedido1.setDataPedido(LocalDateTime.now());
        pedido1.setStatus(StatusPedido.ENTREGUE);
        pedido1.setEnderecoEntrega(String.format("%s, %s", cliente1.getEndereco().getLogradouro(), cliente1.getEndereco().getNumero()));
        ItemPedido item1_1 = new ItemPedido(null, pedido1, prod1, 1, prod1.getPreco());
        ItemPedido item1_2 = new ItemPedido(null, pedido1, prod5, 2, prod5.getPreco());
        pedido1.setItens(Arrays.asList(item1_1, item1_2));
        BigDecimal totalItens1 = item1_1.getPrecoUnitario().multiply(new BigDecimal(item1_1.getQuantidade())).add(item1_2.getPrecoUnitario().multiply(new BigDecimal(item1_2.getQuantidade())));
        pedido1.setValorTotal(totalItens1.add(rest1.getTaxaEntrega()));
        pedidoRepository.save(pedido1);

        Pedido pedido2 = new Pedido();
        pedido2.setCliente(cliente2);
        pedido2.setRestaurante(rest2);
        pedido2.setDataPedido(LocalDateTime.now().minusHours(1));
        pedido2.setStatus(StatusPedido.EM_PREPARO);
        pedido2.setEnderecoEntrega(String.format("%s, %s", cliente2.getEndereco().getLogradouro(), cliente2.getEndereco().getNumero()));
        ItemPedido item2_1 = new ItemPedido(null, pedido2, prod3, 1, prod3.getPreco());
        pedido2.setItens(List.of(item2_1));
        BigDecimal totalItens2 = item2_1.getPrecoUnitario().multiply(new BigDecimal(item2_1.getQuantidade()));
        pedido2.setValorTotal(totalItens2.add(rest2.getTaxaEntrega()));
        pedidoRepository.save(pedido2);

        System.out.println("\n--- DADOS CARREGADOS ---\n");
    }
}
