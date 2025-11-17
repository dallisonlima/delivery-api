package com.delivery_api.Projeto.Delivery.API.service;

import com.delivery_api.Projeto.Delivery.API.dto.ProdutoRequestDTO;
import com.delivery_api.Projeto.Delivery.API.dto.ProdutoResponseDTO;
import com.delivery_api.Projeto.Delivery.API.entity.Produto;
import com.delivery_api.Projeto.Delivery.API.entity.Restaurante;
import com.delivery_api.Projeto.Delivery.API.repository.ProdutoRepository;
import com.delivery_api.Projeto.Delivery.API.repository.RestauranteRepository;
import com.delivery_api.Projeto.Delivery.API.exception.EntityNotFoundException; // Importar
import com.delivery_api.Projeto.Delivery.API.exception.BusinessException;    // Importar
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    public ProdutoResponseDTO cadastrar(ProdutoRequestDTO produtoDTO) {
        Restaurante restaurante = restauranteRepository.findById(produtoDTO.getRestauranteId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado: " + produtoDTO.getRestauranteId()));

        Produto produto = new Produto();
        produto.setNome(produtoDTO.getNome());
        produto.setDescricao(produtoDTO.getDescricao());
        produto.setPreco(produtoDTO.getPreco());
        produto.setCategoria(produtoDTO.getCategoria());
        produto.setDisponivel(produtoDTO.getDisponivel());
        produto.setRestaurante(restaurante);

        Produto novoProduto = produtoRepository.save(produto);
        return toProdutoResponseDTO(novoProduto);
    }

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
        produto.setRestaurante(restaurante);

        Produto produtoAtualizado = produtoRepository.save(produto);
        return toProdutoResponseDTO(produtoAtualizado);
    }

    public void alterarDisponibilidade(Long produtoId) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + produtoId));
        produto.setDisponivel(!produto.getDisponivel()); // Alterna o status
        produtoRepository.save(produto);
    }

    public void deletar(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new EntityNotFoundException("Produto não encontrado: " + id);
        }
        produtoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> buscarProdutosPorRestaurante(Long restauranteId) {
        return produtoRepository.findByRestauranteIdAndDisponivelTrue(restauranteId).stream()
                .map(this::toProdutoResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ProdutoResponseDTO> buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .filter(Produto::getDisponivel)
                .map(this::toProdutoResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> buscarProdutosPorCategoria(String categoria) {
        return produtoRepository.findByCategoria(categoria).stream()
                .map(this::toProdutoResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> buscarPorNome(String nome) {
        return produtoRepository.findByNomeContainingIgnoreCase(nome).stream()
                .map(this::toProdutoResponseDTO)
                .collect(Collectors.toList());
    }

    private ProdutoResponseDTO toProdutoResponseDTO(Produto produto) {
        ProdutoResponseDTO dto = new ProdutoResponseDTO();
        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setDescricao(produto.getDescricao());
        dto.setPreco(produto.getPreco());
        dto.setCategoria(produto.getCategoria());
        dto.setDisponivel(produto.getDisponivel());
        dto.setRestauranteId(produto.getRestaurante().getId());
        dto.setNomeRestaurante(produto.getRestaurante().getNome());
        return dto;
    }
}
