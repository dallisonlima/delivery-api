package com.delivery_api.Projeto.Delivery.API.service;

import com.delivery_api.Projeto.Delivery.API.entity.Restaurante;
import com.delivery_api.Projeto.Delivery.API.repository.RestauranteRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
class RestauranteServiceCacheTest {

    @Autowired
    private RestauranteService restauranteService;

    @SpyBean
    private RestauranteRepository restauranteRepository;

    @Autowired
    private CacheManager cacheManager;

    private Restaurante restaurante;

    @BeforeEach
    void setUp() {
        restaurante = new Restaurante();
        restaurante.setNome("Restaurante para Teste de Cache");
        restaurante = restauranteRepository.save(restaurante);
    }

    @AfterEach
    void tearDown() {
        cacheManager.getCache("restaurantes").clear();
    }

    @Test
    void buscarPorId_DeveCachearResultado() {
        // 1ª Chamada: Deve ir ao banco de dados
        restauranteService.buscarPorId(restaurante.getId());

        // 2ª Chamada: Deve retornar do cache
        restauranteService.buscarPorId(restaurante.getId());

        // Verifica se o método do repositório foi chamado apenas uma vez
        verify(restauranteRepository, times(1)).findById(restaurante.getId());
    }

    @Test
    void atualizar_DeveInvalidarCache() {
        // 1. Popula o cache
        restauranteService.buscarPorId(restaurante.getId());

        // 2. Invalida o cache
        restauranteService.ativarOuDesativar(restaurante.getId(), false);

        // 3. Busca novamente, deve ir ao banco de dados
        restauranteService.buscarPorId(restaurante.getId());

        // Verifica se o método do repositório foi chamado duas vezes no total
        verify(restauranteRepository, times(2)).findById(restaurante.getId());
    }
}
