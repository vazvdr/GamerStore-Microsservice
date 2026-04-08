package com.gamerstore.product_service.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gamerstore.product_service.dto.ProductResponseDTO;
import com.gamerstore.product_service.entity.Product;
import com.gamerstore.product_service.repositories.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    // ===============================
    // 🔽 Métodos de listagem e busca (sem alteração)
    // ===============================
    @Test
    @DisplayName("findAll | Deve retornar lista vazia quando não houver produtos")
    void findAll_shouldReturnEmptyList() {
        when(productRepository.findAll()).thenReturn(List.of());

        List<ProductResponseDTO> result = productService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAll | Deve retornar lista de produtos mapeados")
    void findAll_shouldReturnProductList() {
        Product product = mockProduct();

        when(productRepository.findAll()).thenReturn(List.of(product));

        List<ProductResponseDTO> result = productService.findAll();

        assertEquals(1, result.size());
        assertEquals(product.getName(), result.get(0).name());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findById | Deve retornar produto quando existir")
    void findById_shouldReturnProduct() {
        Product product = mockProduct();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponseDTO dto = productService.findById(1L);

        assertNotNull(dto);
        assertEquals(product.getName(), dto.name());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findById | Deve lançar exceção quando produto não existir")
    void findById_shouldThrowExceptionWhenNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> productService.findById(99L)
        );

        assertEquals("Produto não encontrado com id: 99", exception.getMessage());
        verify(productRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("search | Deve retornar produtos que correspondem ao termo")
    void search_shouldReturnMatchingProducts() {
        Product product = mockProduct();
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<ProductResponseDTO> result = productService.search("logitech");

        assertEquals(1, result.size());
        assertEquals(product.getName(), result.get(0).name());
    }

    @Test
    @DisplayName("search | Deve ignorar acentos e letras maiúsculas")
    void search_shouldIgnoreAccentsAndCase() {
        Product product = mockProduct();
        product.setName("Tecládo Mecânico");
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<ProductResponseDTO> result = productService.search("teclado");

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("search | Deve retornar lista vazia quando não houver match")
    void search_shouldReturnEmptyListWhenNoMatch() {
        Product product = mockProduct();
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<ProductResponseDTO> result = productService.search("monitor");

        assertTrue(result.isEmpty());
    }

    private Product mockProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Mouse Gamer");
        product.setBrand("Logitech");
        product.setModel("G502");
        product.setDescription("Mouse gamer profissional");
        product.setTags("mouse gamer fps");
        product.setStock(10);
        return product;
    }

    // ===============================
    // 🔽 REDUCE STOCK
    // ===============================
    @Test
    @DisplayName("reduceStock | Deve reduzir o estoque corretamente")
    void reduceStock_shouldDecreaseStock() {
        Product product = mockProduct();
        product.setStock(10);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.reduceStock(1L, 3);

        assertEquals(7, product.getStock());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("reduceStock | Deve lançar exceção quando produto não existir")
    void reduceStock_shouldThrowWhenProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productService.reduceStock(1L, 1));
    }

    // ===============================
    // 🔽 RESERVE STOCK (COM EVENTID)
    // ===============================
    @Test
    @DisplayName("reserveStock | Deve reservar estoque corretamente")
    void reserveStock_shouldDecreaseStock() {
        Product product = mockProduct();
        product.setStock(10);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        String eventId = "reserve-test-1";
        productService.reserveStock(1L, 3, eventId);

        assertEquals(7, product.getStock());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("reserveStock | Não deve processar evento duplicado")
    void reserveStock_shouldNotProcessDuplicateEvent() {
        Product product = mockProduct();
        product.setStock(10);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        String eventId = "reserve-test-dup";
        productService.reserveStock(1L, 3, eventId);
        productService.reserveStock(1L, 3, eventId); // duplicado

        verify(productRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("reserveStock | Deve lançar exceção quando estoque insuficiente")
    void reserveStock_shouldThrowWhenInsufficientStock() {
        Product product = mockProduct();
        product.setStock(2);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        String eventId = "reserve-test-insufficient";
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                productService.reserveStock(1L, 5, eventId));

        assertTrue(ex.getMessage().contains("Estoque insuficiente"));
    }

    @Test
    @DisplayName("reserveStock | Deve lançar exceção quando produto não existir")
    void reserveStock_shouldThrowWhenProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        String eventId = "reserve-test-nofound";
        assertThrows(RuntimeException.class, () ->
                productService.reserveStock(1L, 1, eventId));
    }

    // ===============================
    // 🔓 RELEASE RESERVATION (COM EVENTID)
    // ===============================
    @Test
    @DisplayName("releaseReservation | Deve liberar estoque corretamente")
    void releaseReservation_shouldIncreaseStock() {
        Product product = mockProduct();
        product.setStock(5);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        String eventId = "release-test-1";
        productService.releaseReservation(1L, 3, eventId);

        assertEquals(8, product.getStock());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("releaseReservation | Não deve processar evento duplicado")
    void releaseReservation_shouldNotProcessDuplicateEvent() {
        Product product = mockProduct();
        product.setStock(5);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        String eventId = "release-test-dup";
        productService.releaseReservation(1L, 3, eventId);
        productService.releaseReservation(1L, 3, eventId); // duplicado

        verify(productRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("releaseReservation | Deve lançar exceção quando produto não existir")
    void releaseReservation_shouldThrowWhenProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        String eventId = "release-test-nofound";
        assertThrows(RuntimeException.class, () ->
                productService.releaseReservation(1L, 1, eventId));
    }
}