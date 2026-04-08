package com.gamerstore.product_service.services;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

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

    public List<ProductResponseDTO> findAll() {
        return productRepository.findAll()
                .stream()
                .map(ProductMapper::toDTO)
                .toList();
    }

    public ProductResponseDTO findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + id));
        return ProductMapper.toDTO(product);
    }

    public List<ProductResponseDTO> search(String query) {
        String normalizedQuery = TextNormalizer.normalize(query);

        return productRepository.findAll()
                .stream()
                .filter(product -> {

                    // Texto completo para busca (string)
                    String searchableText = String.join(" ",
                            product.getName() != null ? product.getName() : "",
                            product.getBrand() != null ? product.getBrand() : "",
                            product.getModel() != null ? product.getModel() : "",
                            product.getDescription() != null ? product.getDescription() : "",
                            product.getTags() != null ? product.getTags() : "");

                    String normalizedProductText = TextNormalizer.normalize(searchableText);

                    // Busca textual (nome, descrição, marca, etc)
                    boolean matchesText = normalizedProductText.contains(normalizedQuery);

                    // Busca por ID (se for número)
                    boolean matchesId = false;
                    try {
                        Long queryId = Long.parseLong(query);
                        matchesId = product.getId().equals(queryId);
                    } catch (NumberFormatException ignored) {
                    }

                    // Busca por preço (ex: "5000")
                    boolean matchesPrice = false;
                    try {
                        Double queryPrice = Double.parseDouble(query);
                        matchesPrice = product.getPrice().equals(queryPrice);
                    } catch (NumberFormatException ignored) {
                    }

                    return matchesText || matchesId || matchesPrice;
                })
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