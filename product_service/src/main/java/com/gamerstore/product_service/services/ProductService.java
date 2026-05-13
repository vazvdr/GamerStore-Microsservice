package com.gamerstore.product_service.services;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.gamerstore.product_service.dto.ProductResponseDTO;
import com.gamerstore.product_service.entity.Product;
import com.gamerstore.product_service.mapper.ProductMapper;
import com.gamerstore.product_service.repositories.ProductRepository;
import com.gamerstore.product_service.utils.TextNormalizer;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    // Idempotência: controla eventos processados
    private final Set<String> processedEvents = ConcurrentHashMap.newKeySet();

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Cacheable(value = "products", key = "'page:' + #page + ':size:' + #size")
    public Page<ProductResponseDTO> findAll(
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size);

        return productRepository
                .findAll(pageable)
                .map(ProductMapper::toDTO);
    }

    @Cacheable(value = "products", key = "#id")
    public ProductResponseDTO findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + id));
        return ProductMapper.toDTO(product);
    }

    @Cacheable(value = "product-search", key = "#query")
    public List<ProductResponseDTO> search(String query) {

        String normalizedQuery = TextNormalizer.normalize(query);

        return productRepository.searchGeneral(normalizedQuery)
                .stream()
                .map(ProductMapper::toDTO)
                .toList();
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