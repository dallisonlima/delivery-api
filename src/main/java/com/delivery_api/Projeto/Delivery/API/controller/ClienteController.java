package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.ClienteRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.ClienteResponseDTO;
import com.delivery_api.Projeto.Delivery.API.dto.PedidoResponseDTO;
import com.delivery_api.Projeto.Delivery.API.dto.response.ApiResponseWrapper;
import com.delivery_api.Projeto.Delivery.API.service.ClienteService;
import com.delivery_api.Projeto.Delivery.API.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "409", description = "Cliente já cadastrado")
    })
    public ResponseEntity<ApiResponseWrapper<ClienteResponseDTO>> cadastrar(@Validated @RequestBody ClienteRequestDTO clienteDTO) {
        ClienteResponseDTO clienteSalvo = clienteService.cadastrar(clienteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseWrapper.success(clienteSalvo, "Cliente cadastrado com sucesso."));
    }

    @GetMapping
    @Operation(summary = "Lista todos os clientes ativos", description = "Lista todos os clientes que estão com o status ativo.")
    @ApiResponse(responseCode = "200", description = "Clientes listados com sucesso")
    public ResponseEntity<ApiResponseWrapper<List<ClienteResponseDTO>>> listarAtivos() {
        List<ClienteResponseDTO> clientes = clienteService.listarAtivos();
        return ResponseEntity.ok(ApiResponseWrapper.success(clientes, "Clientes listados com sucesso."));
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
    @Operation(summary = "Buscar clientes por nome", description = "Recupera uma lista de clientes que correspondem ao nome fornecido")
    @ApiResponse(responseCode = "200", description = "Clientes encontrados")
    public ResponseEntity<ApiResponseWrapper<List<ClienteResponseDTO>>> buscarPorNome(@Param("nome") String nome) {
        List<ClienteResponseDTO> clientes = clienteService.buscarPorNome(nome);
        return ResponseEntity.ok(ApiResponseWrapper.success(clientes, "Clientes encontrados com sucesso."));
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
    @Operation(summary = "Busca os pedidos de um cliente", description = "Busca todos os pedidos de um cliente específico.")
    @ApiResponse(responseCode = "200", description = "Pedidos do cliente listados com sucesso")
    public ResponseEntity<ApiResponseWrapper<List<PedidoResponseDTO>>> buscarPedidosPorCliente(@Parameter(description = "ID do cliente") @PathVariable Long clienteId) {
        List<PedidoResponseDTO> pedidos = pedidoService.buscarPedidosPorCliente(clienteId);
        return ResponseEntity.ok(ApiResponseWrapper.success(pedidos, "Pedidos do cliente listados com sucesso."));
    }
}
