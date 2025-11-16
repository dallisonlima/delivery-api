package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.entity.Cliente;
import com.delivery_api.Projeto.Delivery.API.entity.Pedido;
import com.delivery_api.Projeto.Delivery.API.entity.StatusPedido;
import com.delivery_api.Projeto.Delivery.API.repository.PedidoRepository;
import com.delivery_api.Projeto.Delivery.API.service.ClienteService;
import com.delivery_api.Projeto.Delivery.API.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private PedidoRepository pedidoRepository;

    @PostMapping
    public ResponseEntity<Cliente> cadastrar(@Validated @RequestBody Cliente cliente) {
        Cliente clienteSalvo = clienteService.cadastrar(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteSalvo);
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> listarAtivos() {
        return ResponseEntity.ok(clienteService.listarAtivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable Long id) {
        return clienteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> atualizar(@PathVariable Long id, @Validated @RequestBody Cliente cliente) {
        Cliente clienteAtualizado = clienteService.atualizar(id, cliente);
        return ResponseEntity.ok(clienteAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        clienteService.inativar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{clienteId}/pedidos")
    public ResponseEntity<List<Pedido>> buscarPedidosPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(pedidoRepository.findByClienteId(clienteId));
    }

    @PatchMapping("/{clienteId}/pedidos/{pedidoId}/status")
    public ResponseEntity<Pedido> atualizarStatusPedido(
            @PathVariable Long clienteId,
            @PathVariable Long pedidoId,
            @RequestParam StatusPedido status) {
        Pedido pedidoAtualizado = pedidoService.alterarStatusParaCliente(clienteId, pedidoId, status);
        return ResponseEntity.ok(pedidoAtualizado);
    }
}
