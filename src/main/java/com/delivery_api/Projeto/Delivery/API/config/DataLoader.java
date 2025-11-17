package com.delivery_api.Projeto.Delivery.API.config;

import com.delivery_api.Projeto.Delivery.API.entity.*;
import com.delivery_api.Projeto.Delivery.API.repository.ClienteRepository;
import com.delivery_api.Projeto.Delivery.API.repository.PedidoRepository;
import com.delivery_api.Projeto.Delivery.API.repository.ProdutoRepository;
import com.delivery_api.Projeto.Delivery.API.repository.RestauranteRepository;
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
        // Clientes
        Cliente cliente1 = new Cliente(null, "Carlos Souza", "carlos@example.com", "11987654321", "Rua A, 123", LocalDateTime.now(), true);
        Cliente cliente2 = new Cliente(null, "Ana Pereira", "ana@example.com", "11912345678", "Rua B, 456", LocalDateTime.now(), true);
        Cliente cliente3 = new Cliente(null, "Beatriz Costa", "beatriz@example.com", "11955554444", "Rua C, 789", LocalDateTime.now(), true);
        clienteRepository.saveAll(Arrays.asList(cliente1, cliente2, cliente3));

        // Restaurantes
        Restaurante rest1 = new Restaurante(null, "Pizzaria Delícia", new BigDecimal("5.00"), "Pizza", true, "Av. Principal, 1", 4.5, "1122223333");
        Restaurante rest2 = new Restaurante(null, "Sushi House", new BigDecimal("10.00"), "Japonesa", true, "Av. Secundária, 2", 4.8, "1144445555");
        restauranteRepository.saveAll(Arrays.asList(rest1, rest2));

        // Produtos
        Produto prod1 = new Produto(null, "Pizza Margherita", "Molho, muçarela e manjericão", new BigDecimal("45.00"), "Pizza", true, rest1);
        Produto prod2 = new Produto(null, "Pizza Calabresa", "Muçarela e calabresa", new BigDecimal("50.00"), "Pizza", true, rest1);
        Produto prod3 = new Produto(null, "Combinado Salmão", "20 peças de salmão", new BigDecimal("80.00"), "Japonesa", true, rest2);
        Produto prod4 = new Produto(null, "Temaki Filadélfia", "Salmão, cream cheese e cebolinha", new BigDecimal("25.00"), "Japonesa", true, rest2);
        Produto prod5 = new Produto(null, "Refrigerante Lata", "Coca-Cola", new BigDecimal("6.00"), "Bebida", true, rest1);
        produtoRepository.saveAll(Arrays.asList(prod1, prod2, prod3, prod4, prod5));

        // Pedidos
        Pedido pedido1 = new Pedido();
        pedido1.setCliente(cliente1);
        pedido1.setRestaurante(rest1);
        pedido1.setDataPedido(LocalDateTime.now());
        pedido1.setStatus(StatusPedido.ENTREGUE);
        pedido1.setEnderecoEntrega(cliente1.getEndereco());
        ItemPedido item1_1 = new ItemPedido(null, pedido1, prod1, 1, prod1.getPreco());
        ItemPedido item1_2 = new ItemPedido(null, pedido1, prod5, 2, prod5.getPreco());
        pedido1.setItens(Arrays.asList(item1_1, item1_2));
        BigDecimal totalItens1 = item1_1.getPrecoUnitario().multiply(new BigDecimal(item1_1.getQuantidade())).add(item1_2.getPrecoUnitario().multiply(new BigDecimal(item1_2.getQuantidade())));
        pedido1.setValorTotal(totalItens1.add(rest1.getTaxaEntrega()));
        pedido1.setItensDescricao("Pizza Margherita (1x), Refrigerante Lata (2x)");
        pedidoRepository.save(pedido1);

        Pedido pedido2 = new Pedido();
        pedido2.setCliente(cliente2);
        pedido2.setRestaurante(rest2);
        pedido2.setDataPedido(LocalDateTime.now().minusHours(1));
        pedido2.setStatus(StatusPedido.EM_PREPARO);
        pedido2.setEnderecoEntrega(cliente2.getEndereco());
        ItemPedido item2_1 = new ItemPedido(null, pedido2, prod3, 1, prod3.getPreco());
        pedido2.setItens(List.of(item2_1));
        BigDecimal totalItens2 = item2_1.getPrecoUnitario().multiply(new BigDecimal(item2_1.getQuantidade()));
        pedido2.setValorTotal(totalItens2.add(rest2.getTaxaEntrega()));
        pedido2.setItensDescricao("Combinado Salmão (1x)");
        pedidoRepository.save(pedido2);

        System.out.println("\n--- DADOS CARREGADOS ---\n");
        
        System.out.println("--- TESTANDO CONSULTAS ---\n");

        System.out.println(">> ClienteRepository");
        clienteRepository.findByEmail("ana@example.com").ifPresent(c -> System.out.println("Busca por e-mail 'ana@example.com': " + c.getNome()));
        System.out.println("Clientes ativos: " + clienteRepository.findByAtivoTrue().size());
        System.out.println("Busca por nome 'Souza': " + clienteRepository.findByNomeContainingIgnoreCase("Souza").size());
        System.out.println("E-mail 'carlos@example.com' existe? " + clienteRepository.existsByEmail("carlos@example.com"));
        System.out.println("------------------------\n");

        System.out.println(">> RestauranteRepository");
        System.out.println("Busca por categoria 'Pizza': " + restauranteRepository.findByCategoria("Pizza").size());
        System.out.println("Restaurantes ativos: " + restauranteRepository.findByAtivoTrue().size());
        System.out.println("Restaurantes com taxa de entrega <= 5.00: " + restauranteRepository.findByTaxaEntregaLessThanEqual(new BigDecimal("5.00")).size());
        System.out.println("Top 5 restaurantes por nome: " + restauranteRepository.findTop5ByOrderByNomeAsc().size());
        System.out.println("------------------------\n");

        System.out.println(">> ProdutoRepository");
        System.out.println("Produtos do Restaurante 1: " + produtoRepository.findByRestauranteId(rest1.getId()).size());
        System.out.println("Produtos disponíveis: " + produtoRepository.findByDisponivelTrue().size());
        System.out.println("Produtos da categoria 'Japonesa': " + produtoRepository.findByCategoria("Japonesa").size());
        System.out.println("Produtos com preço <= 25.00: " + produtoRepository.findByPrecoLessThanEqual(new BigDecimal("25.00")).size());
        System.out.println("------------------------\n");

        System.out.println(">> PedidoRepository");
        System.out.println("Pedidos do Cliente 1: " + pedidoRepository.findByClienteId(cliente1.getId()).size());
        System.out.println("Pedidos com status 'ENTREGUE': " + pedidoRepository.findByStatus(StatusPedido.ENTREGUE).size());
        System.out.println("Top 10 pedidos mais recentes: " + pedidoRepository.findTop10ByOrderByDataPedidoDesc().size());
        System.out.println("Pedidos nas últimas 24h: " + pedidoRepository.findByDataPedidoBetween(LocalDateTime.now().minusDays(1), LocalDateTime.now()).size());
        System.out.println("------------------------\n");
    }
}
