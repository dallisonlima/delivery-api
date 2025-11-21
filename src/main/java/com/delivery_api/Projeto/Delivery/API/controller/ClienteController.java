package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.ClienteRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.ClienteResponseDTO;
import com.delivery_api.Projeto.Delivery.API.dto.PedidoResponseDTO;
import com.delivery_api.Projeto.Delivery.API.dto.response.ApiResponseWrapper;
import com.delivery_api.Projeto.Delivery.API.dto.response.PagedResponseWrapper;
import com.delivery_api.Projeto.Delivery.API.service.ClienteService;
import com.delivery_api.Projeto.Delivery.API.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Operações relacionadas a clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    @Operation(summary = "Cadastra um novo cliente", description = "Cadastra um novo cliente com base nas informações fornecidas.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "409", description = "Cliente já cadastrado")
    })
    public ResponseEntity<ApiResponseWrapper<ClienteResponseDTO>> cadastrar(@Validated @RequestBody ClienteRequestDTO clienteDTO) {
        ClienteResponseDTO clienteSalvo = clienteService.cadastrar(clienteDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(clienteSalvo.getId())
                .toUri();

        return ResponseEntity.created(location).body(ApiResponseWrapper.success(clienteSalvo, "Cliente cadastrado com sucesso."));
    }

    @GetMapping
    @Operation(summary = "Lista todos os clientes ativos de forma paginada", description = "Lista todos os clientes que estão com o status ativo.")
    @ApiResponse(responseCode = "200", description = "Clientes listados com sucesso")
    public ResponseEntity<PagedResponseWrapper<ClienteResponseDTO>> listarAtivos(@PageableDefault(size = 10) Pageable pageable) {
        Page<ClienteResponseDTO> clientes = clienteService.listarAtivos(pageable);
        return ResponseEntity.ok(new PagedResponseWrapper<>(clientes));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um cliente por ID", description = "Busca um cliente específico pelo seu ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<ClienteResponseDTO>> buscarPorId(@Parameter(description = "ID do cliente") @PathVariable Long id) {
        ClienteResponseDTO cliente = clienteService.buscarPorId(id);
        return ResponseEntity.ok(ApiResponseWrapper.success(cliente, "Cliente encontrado com sucesso."));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Buscar cliente por email", description = "Recupera os detalhes de um cliente específico pelo email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<ClienteResponseDTO>> buscarPorEmail(@PathVariable String email) {
        ClienteResponseDTO cliente = clienteService.buscarPorEmail(email);
        return ResponseEntity.ok(ApiResponseWrapper.success(cliente, "Cliente encontrado com sucesso."));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar clientes por nome de forma paginada", description = "Recupera uma lista de clientes que correspondem ao nome fornecido.")
    @ApiResponse(responseCode = "200", description = "Clientes encontrados")
    public ResponseEntity<PagedResponseWrapper<ClienteResponseDTO>> buscarPorNome(
            @RequestParam String nome,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<ClienteResponseDTO> clientes = clienteService.buscarPorNome(nome, pageable);
        return ResponseEntity.ok(new PagedResponseWrapper<>(clientes));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um cliente", description = "Atualiza um cliente específico com base nas informações fornecidas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<ApiResponseWrapper<ClienteResponseDTO>> atualizar(@Parameter(description = "ID do cliente") @PathVariable Long id, @Validated @RequestBody ClienteRequestDTO clienteDTO) {
        ClienteResponseDTO clienteAtualizado = clienteService.atualizar(id, clienteDTO);
        return ResponseEntity.ok(ApiResponseWrapper.success(clienteAtualizado, "Cliente atualizado com sucesso."));
    }

    @PatchMapping("/{id}/toggle-status")
    @Operation(summary = "Ativa ou desativa um cliente", description = "Ativa ou desativa um cliente específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status do cliente alterado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<ClienteResponseDTO>> ativarDesativarCliente(@Parameter(description = "ID do cliente") @PathVariable Long id) {
        ClienteResponseDTO clienteAtualizado = clienteService.ativarDesativarCliente(id);
        return ResponseEntity.ok(ApiResponseWrapper.success(clienteAtualizado, "Status do cliente alterado com sucesso."));
    }

    @GetMapping("/{clienteId}/pedidos")
    @Operation(summary = "Busca os pedidos de um cliente de forma paginada", description = "Busca todos os pedidos de um cliente específico.")
    @ApiResponse(responseCode = "200", description = "Pedidos do cliente listados com sucesso")
    public ResponseEntity<PagedResponseWrapper<PedidoResponseDTO>> buscarPedidosPorCliente(
            @Parameter(description = "ID do cliente") @PathVariable Long clienteId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<PedidoResponseDTO> pedidos = pedidoService.buscarPedidosPorCliente(clienteId, pageable);
        return ResponseEntity.ok(new PagedResponseWrapper<>(pedidos));
    }
}
