package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.ProdutoRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.ProdutoResponseDTO;
import com.delivery_api.Projeto.Delivery.API.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException; // Importar a exceção
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
@CrossOrigin(origins = "*")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> cadastrar(@Validated @RequestBody ProdutoRequestDTO produtoDTO) {
        ProdutoResponseDTO novoProduto = produtoService.cadastrar(produtoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoProduto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizar(@PathVariable Long id, @Validated @RequestBody ProdutoRequestDTO produtoDTO) {
        ProdutoResponseDTO produtoAtualizado = produtoService.atualizar(id, produtoDTO);
        return ResponseEntity.ok(produtoAtualizado);
    }

    @GetMapping("/restaurante/{restauranteId}")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorRestaurante(@PathVariable Long restauranteId) {
        return ResponseEntity.ok(produtoService.buscarProdutosPorRestaurante(restauranteId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable Long id) {
        return produtoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarProdutosPorCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(produtoService.buscarProdutosPorCategoria(categoria));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletar(@PathVariable Long id) { // Alterado o tipo de retorno para String
        try {
            produtoService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (DataIntegrityViolationException e) {
            // Retorna 409 Conflict ou 400 Bad Request com uma mensagem explicativa
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Não é possível deletar o produto pois ele está associado a pedidos. Considere alterar sua disponibilidade.");
        }
    }

    @PatchMapping("/{id}/toggle-disponibilidade")
    public ResponseEntity<Void> alterarDisponibilidade(@PathVariable Long id) {
        produtoService.alterarDisponibilidade(id);
        return ResponseEntity.noContent().build();
    }
}
