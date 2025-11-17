package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.ClienteRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.ClienteResponseDTO;
import com.delivery_api.Projeto.Delivery.API.dto.PedidoResponseDTO;
import com.delivery_api.Projeto.Delivery.API.service.ClienteService;
import com.delivery_api.Projeto.Delivery.API.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
@Tag(name = "Clientes", description = "Operações relacionadas a clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    @Operation(summary = "Cadastra um novo cliente", description = "Cadastra um novo cliente com base nas informações fornecidas.")
    @ApiResponse(responseCode = "201", description = "Cliente cadastrado com sucesso")
    public ResponseEntity<ClienteResponseDTO> cadastrar(@Validated @RequestBody ClienteRequestDTO clienteDTO) {
        ClienteResponseDTO clienteSalvo = clienteService.cadastrar(clienteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteSalvo);
    }

    @GetMapping
    @Operation(summary = "Lista todos os clientes ativos", description = "Lista todos os clientes que estão com o status ativo.")
    @ApiResponse(responseCode = "200", description = "Clientes listados com sucesso")
    public ResponseEntity<List<ClienteResponseDTO>> listarAtivos() {
        return ResponseEntity.ok(clienteService.listarAtivos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um cliente por ID", description = "Busca um cliente específico pelo seu ID.")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado com sucesso")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(@Parameter(description = "ID do cliente") @PathVariable Long id) {
        return clienteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um cliente", description = "Atualiza um cliente específico com base nas informações fornecidas.")
    @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso")
    public ResponseEntity<ClienteResponseDTO> atualizar(@Parameter(description = "ID do cliente") @PathVariable Long id, @Validated @RequestBody ClienteRequestDTO clienteDTO) {
        ClienteResponseDTO clienteAtualizado = clienteService.atualizar(id, clienteDTO);
        return ResponseEntity.ok(clienteAtualizado);
    }

    @PatchMapping("/{id}/toggle-status")
    @Operation(summary = "Ativa ou desativa um cliente", description = "Ativa ou desativa um cliente específico.")
    @ApiResponse(responseCode = "204", description = "Status do cliente alterado com sucesso")
    public ResponseEntity<Void> ativarDesativarCliente(@Parameter(description = "ID do cliente") @PathVariable Long id) {
        clienteService.ativarDesativarCliente(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{clienteId}/pedidos")
    @Operation(summary = "Busca os pedidos de um cliente", description = "Busca todos os pedidos de um cliente específico.")
    @ApiResponse(responseCode = "200", description = "Pedidos do cliente listados com sucesso")
    public ResponseEntity<List<PedidoResponseDTO>> buscarPedidosPorCliente(@Parameter(description = "ID do cliente") @PathVariable Long clienteId) {
        return ResponseEntity.ok(pedidoService.buscarPedidosPorCliente(clienteId));
    }
}
