package com.gamerstore.product_service.services;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.gamerstore.product_service.dto.ProductResponseDTO;
import com.gamerstore.product_service.entity.Product;
import com.gamerstore.product_service.entity.elastic.ProductDocument;
import com.gamerstore.product_service.mapper.ProductElasticMapper;
import com.gamerstore.product_service.mapper.ProductMapper;
import com.gamerstore.product_service.repositories.ProductRepository;
import com.gamerstore.product_service.repositories.elastic.ProductElasticRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductElasticRepository elasticRepository;
    private final ProductElasticMapper productElasticMapper;
    // Idempotência: controla eventos processados
    private final Set<String> processedEvents = ConcurrentHashMap.newKeySet();

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    public ProductService(ProductRepository productRepository,
            ProductElasticRepository elasticRepository,
            ProductElasticMapper productElasticMapper) {
        this.productRepository = productRepository;
        this.elasticRepository = elasticRepository;
        this.productElasticMapper = productElasticMapper;
    }

    @Cacheable(value = "products", key = "'all'")
    public List<ProductResponseDTO> findAll() {
        return productRepository.findAll()
                .stream()
                .map(ProductMapper::toDTO)
                .toList();
    }

    @Cacheable(value = "products", key = "#id")
    public ProductResponseDTO findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + id));
        return ProductMapper.toDTO(product);
    }

    public List<ProductResponseDTO> search(String query) {

        long start = System.currentTimeMillis();

        log.info("🔍 Iniciando busca por: '{}'", query);

        // 1. Busca no Elastic
        List<Long> productIds = elasticRepository
                .findByNameContainingOrDescriptionContainingOrBrandContainingOrModelContainingOrTagsContaining(
                        query, query, query, query, query)
                .stream()
                .map(ProductDocument::getId)
                .toList();

        long elasticTime = System.currentTimeMillis();

        log.info("⚡ ElasticSearch retornou {} IDs em {} ms",
                productIds.size(),
                (elasticTime - start));

        if (productIds.isEmpty()) {
            log.warn("❌ Nenhum produto encontrado no Elastic para: '{}'", query);
            return List.of();
        }

        // 2. Busca no banco
        List<ProductResponseDTO> result = productRepository.findAllById(productIds)
                .stream()
                .map(ProductMapper::toDTO)
                .toList();

        long end = System.currentTimeMillis();

        log.info("💾 PostgreSQL retornou {} produtos em {} ms",
                result.size(),
                (end - elasticTime));

        log.info("🚀 Tempo total da busca: {} ms", (end - start));

        return result;
    }

    public void reduceStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }

    public List<ProductResponseDTO> getProductsByIds(List<Long> ids) {
        List<Product> products = productRepository.findAllById(ids);
        return products.stream()
                .map(ProductMapper::toDTO)
                .toList();
    }

    // ===============================
    // RESERVA DE ESTOQUE COM EVENT ID
    // ===============================
    public void reserveStock(Long productId, Integer quantity, String eventId) {
        if (processedEvents.contains(eventId)) {
            return;
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        if (product.getStock() < quantity) {
            throw new RuntimeException("Estoque insuficiente para produto: " + productId);
        }

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);

        processedEvents.add(eventId);
    }

    public void releaseReservation(Long productId, Integer quantity, String eventId) {
        if (processedEvents.contains(eventId)) {
            return;
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        product.setStock(product.getStock() + quantity);
        productRepository.save(product);

        processedEvents.add(eventId);
    }
}