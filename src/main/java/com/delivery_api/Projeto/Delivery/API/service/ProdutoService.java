package com.delivery_api.Projeto.Delivery.API.service;

import com.delivery_api.Projeto.Delivery.API.dto.request.ProdutoRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.response.ProdutoResponseDTO;
import com.delivery_api.Projeto.Delivery.API.entity.Produto;
import com.delivery_api.Projeto.Delivery.API.entity.Restaurante;
import com.delivery_api.Projeto.Delivery.API.entity.Usuario;
import com.delivery_api.Projeto.Delivery.API.repository.ProdutoRepository;
import com.delivery_api.Projeto.Delivery.API.repository.RestauranteRepository;
import com.delivery_api.Projeto.Delivery.API.exception.EntityNotFoundException;
import com.delivery_api.Projeto.Delivery.API.exception.ConflictException;
import com.delivery_api.Projeto.Delivery.API.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @CacheEvict(value = "produtos", allEntries = true)
    public ProdutoResponseDTO cadastrar(ProdutoRequestDTO produtoDTO) {
        Restaurante restaurante = restauranteRepository.findById(produtoDTO.getRestauranteId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado: " + produtoDTO.getRestauranteId()));

        Produto produto = new Produto();
        produto.setNome(produtoDTO.getNome());
        produto.setDescricao(produtoDTO.getDescricao());
        produto.setPreco(produtoDTO.getPreco());
        produto.setCategoria(produtoDTO.getCategoria());
        produto.setDisponivel(produtoDTO.getDisponivel());
        produto.setQuantidadeEstoque(produtoDTO.getQuantidadeEstoque());
        produto.setRestaurante(restaurante);

        Produto novoProduto = produtoRepository.save(produto);
        return toProdutoResponseDTO(novoProduto);
    }

    @CacheEvict(value = "produtos", allEntries = true)
    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO produtoDTO) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + id));

        Restaurante restaurante = restauranteRepository.findById(produtoDTO.getRestauranteId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado: " + produtoDTO.getRestauranteId()));

        produto.setNome(produtoDTO.getNome());
        produto.setDescricao(produtoDTO.getDescricao());
        produto.setPreco(produtoDTO.getPreco());
        produto.setCategoria(produtoDTO.getCategoria());
        produto.setDisponivel(produtoDTO.getDisponivel());
        produto.setQuantidadeEstoque(produtoDTO.getQuantidadeEstoque());
        produto.setRestaurante(restaurante);

        Produto produtoAtualizado = produtoRepository.save(produto);
        return toProdutoResponseDTO(produtoAtualizado);
    }

    @CacheEvict(value = "produtos", allEntries = true)
    public ProdutoResponseDTO alterarDisponibilidade(Long produtoId) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + produtoId));
        produto.setDisponivel(!produto.getDisponivel());
        Produto produtoAtualizado = produtoRepository.save(produto);
        return toProdutoResponseDTO(produtoAtualizado);
    }

    @CacheEvict(value = "produtos", allEntries = true)
    public void deletar(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new EntityNotFoundException("Produto não encontrado: " + id);
        }
        try {
            produtoRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Não é possível deletar o produto pois ele está associado a pedidos. Considere alterar sua disponibilidade.");
        }
    }

    @Cacheable("produtos")
    @Transactional(readOnly = true)
    public Page<ProdutoResponseDTO> buscarProdutosPorRestaurante(Long restauranteId, Pageable pageable) {
        return produtoRepository.findByRestauranteIdAndDisponivelTrue(restauranteId, pageable)
                .map(this::toProdutoResponseDTO);
    }

    @Cacheable(value = "produtos", key = "#id")
    @Transactional(readOnly = true)
    public ProdutoResponseDTO buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .map(this::toProdutoResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + id));
    }

    @Cacheable("produtos")
    @Transactional(readOnly = true)
    public Page<ProdutoResponseDTO> buscarProdutosPorCategoria(String categoria, Pageable pageable) {
        return produtoRepository.findByCategoria(categoria, pageable)
                .map(this::toProdutoResponseDTO);
    }

    @Cacheable("produtos")
    @Transactional(readOnly = true)
    public Page<ProdutoResponseDTO> buscarPorNome(String nome, Pageable pageable) {
        return produtoRepository.findByNomeContainingIgnoreCase(nome, pageable)
                .map(this::toProdutoResponseDTO);
    }

    public boolean isOwner(Long produtoId) {
        Usuario currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null || currentUser.getRestauranteId() == null) {
            return false;
        }

        return produtoRepository.findById(produtoId)
                .map(produto -> Objects.equals(produto.getRestaurante().getId(), currentUser.getRestauranteId()))
                .orElse(false);
    }

    private ProdutoResponseDTO toProdutoResponseDTO(Produto produto) {
        ProdutoResponseDTO dto = new ProdutoResponseDTO();
        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setDescricao(produto.getDescricao());
        dto.setPreco(produto.getPreco());
        dto.setCategoria(produto.getCategoria());
        dto.setDisponivel(produto.getDisponivel());
        dto.setQuantidadeEstoque(produto.getQuantidadeEstoque());
        dto.setRestauranteId(produto.getRestaurante().getId());
        dto.setNomeRestaurante(produto.getRestaurante().getNome());
        return dto;
    }
}
