package com.delivery_api.Projeto.Delivery.API.controller;

import com.delivery_api.Projeto.Delivery.API.dto.PedidoResponseDTO;
import com.delivery_api.Projeto.Delivery.API.dto.ProdutoResponseDTO;
import com.delivery_api.Projeto.Delivery.API.dto.RestauranteRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.RestauranteResponseDTO;
import com.delivery_api.Projeto.Delivery.API.service.PedidoService;
import com.delivery_api.Projeto.Delivery.API.service.ProdutoService;
import com.delivery_api.Projeto.Delivery.API.service.RestauranteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException; // Importar a exceção
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/restaurantes")
@CrossOrigin(origins = "*")
public class RestauranteController {

    @Autowired
    private RestauranteService restauranteService;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<RestauranteResponseDTO> cadastrar(@Validated @RequestBody RestauranteRequestDTO restauranteDTO) {
        RestauranteResponseDTO novoRestaurante = restauranteService.cadastrar(restauranteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoRestaurante);
    }

    @GetMapping
    public ResponseEntity<List<RestauranteResponseDTO>> listar(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Boolean ativo) {
        return ResponseEntity.ok(restauranteService.listar(categoria, ativo));
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<List<RestauranteResponseDTO>> buscarRestaurantesDisponiveis() {
        return ResponseEntity.ok(restauranteService.buscarRestaurantesDisponiveis());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestauranteResponseDTO> buscarPorId(@PathVariable Long id) {
        return restauranteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestauranteResponseDTO> atualizar(@PathVariable Long id, @Validated @RequestBody RestauranteRequestDTO restauranteDTO) {
        RestauranteResponseDTO restauranteAtualizado = restauranteService.atualizar(id, restauranteDTO);
        return ResponseEntity.ok(restauranteAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletar(@PathVariable Long id) {
        try {
            restauranteService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Não é possível deletar o restaurante pois ele possui pedidos associados. Considere inativá-lo.");
        }
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<RestauranteResponseDTO>> buscarPorCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(restauranteService.buscarPorCategoria(categoria));
    }

    @GetMapping("/{restauranteId}/taxa-entrega/{cep}")
    public ResponseEntity<BigDecimal> calcularTaxaEntrega(@PathVariable Long restauranteId, @PathVariable String cep) {
        try {
            BigDecimal taxa = restauranteService.calcularTaxaEntrega(restauranteId, cep);
            return ResponseEntity.ok(taxa);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> ativarOuDesativar(@PathVariable Long id, @RequestParam boolean ativo) {
        if (ativo) {
            restauranteService.ativar(id);
        } else {
            restauranteService.inativar(id);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/proximos/{cep}")
    public ResponseEntity<List<RestauranteResponseDTO>> buscarProximos(@PathVariable String cep) {
        // TODO: Implementar a busca por restaurantes próximos
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{restauranteId}/produtos")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarProdutosPorRestaurante(@PathVariable Long restauranteId) {
        return ResponseEntity.ok(produtoService.buscarProdutosPorRestaurante(restauranteId));
    }

    @GetMapping("/{restauranteId}/pedidos")
    public ResponseEntity<List<PedidoResponseDTO>> buscarPedidosPorRestaurante(@PathVariable Long restauranteId) {
        return ResponseEntity.ok(pedidoService.buscarPedidosPorRestaurante(restauranteId));
    }
}
