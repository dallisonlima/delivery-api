package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.ClienteRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.ClienteResponseDTO;
import com.delivery_api.Projeto.Delivery.API.dto.PedidoResponseDTO;
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
    public ResponseEntity<ClienteResponseDTO> cadastrar(@Validated @RequestBody ClienteRequestDTO clienteDTO) {
        ClienteResponseDTO clienteSalvo = clienteService.cadastrar(clienteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteSalvo);
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listarAtivos() {
        return ResponseEntity.ok(clienteService.listarAtivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable Long id) {
        return clienteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizar(@PathVariable Long id, @Validated @RequestBody ClienteRequestDTO clienteDTO) {
        ClienteResponseDTO clienteAtualizado = clienteService.atualizar(id, clienteDTO);
        return ResponseEntity.ok(clienteAtualizado);
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<Void> ativarDesativarCliente(@PathVariable Long id) {
        clienteService.ativarDesativarCliente(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{clienteId}/pedidos")
    public ResponseEntity<List<PedidoResponseDTO>> buscarPedidosPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(pedidoService.buscarPedidosPorCliente(clienteId));
    }
}
